package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking


class DiscordBot(
    private val environment: Environment,
    private val postingMapper: PostingMapper
) {

    private val logger = KotlinLogging.logger("DiscordBot")
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val postingsFlow = MutableSharedFlow<Posting>()
    private lateinit var kord: Kord

    private val botSettings = Settings.Bot(
        name = environment.get(DISCORD_BOT_NAME),
        token = environment.get(DISCORD_BOT_TOKEN),
        commandPrefix = "!"
    )

    fun start() {
        runBlocking {
            kord = Kord(botSettings.token)

            kord.on<MessageCreateEvent> {
                if (message.author?.isBot == true) return@on
                handleEvent(this)
            }

            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    fun stop() {
        runBlocking {
            kord.logout()
        }
    }

    fun observePostings(): Flow<Posting> = postingsFlow

    suspend fun post(posting: Posting) {
        val snowflake = Snowflake(posting.content.chat.id)

        val request = UserMessageCreateBuilder().apply {
            content = posting.content.extra.mapText()
            attachments = posting.content.extra.mapAttachments(snowflake)
        }.toRequest()

        kord.rest.channel.createMessage(snowflake, request)
    }

    private fun Posting.Content.Extra.mapText(): String? = when (this) {
        is Posting.Content.Extra.Text -> text
        is Posting.Content.Extra.Command -> text
        is Posting.Content.Extra.Urls -> text
        else -> null
    }

    private fun Posting.Content.Extra.mapAttachments(snowflake: Snowflake): MutableList<AttachmentBuilder> {
        val attachment = when (this) {
            is Posting.Content.Extra.Video -> AttachmentBuilder(snowflake).apply { filename = file.name }

            else -> null
        }
        return attachment?.let { mutableListOf(it) } ?: mutableListOf()
    }

    private suspend fun handleEvent(event: Event) {
        val settings = Settings(
            developerName = environment.get(DISCORD_DEVELOPER_NAME),
            bot = botSettings
        )
        val content = postingMapper.map(event, settings)

        val posting = Posting(
            settings = settings,
            content = content,
        )

        postingsFlow.emit(posting)
    }

    private companion object {
        const val DISCORD_DEVELOPER_NAME = "DISCORD_DEVELOPER_NAME"
        const val DISCORD_BOT_NAME = "DISCORD_BOT_NAME"
        const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
    }
}
