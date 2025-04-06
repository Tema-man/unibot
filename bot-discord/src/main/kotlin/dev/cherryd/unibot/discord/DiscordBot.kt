package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.CommandsRepository
import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.interceptor.BotInteractor
import dev.kord.common.entity.AllowedMentionType
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class DiscordBot(
    private val environment: Environment,
    private val postingMapper: PostingMapper,
    private val commandsRepository: CommandsRepository
) : BotInteractor {

    private val logger = KotlinLogging.logger("DiscordBot")
    private val postingsFlow = MutableSharedFlow<Post>()
    private lateinit var kord: Kord
    private val postingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val botSettings = Settings(
        id = environment.get(DISCORD_BOT_ID),
        name = environment.get(DISCORD_BOT_NAME),
        aliases = environment.getBotNameAliases(),
        token = environment.get(DISCORD_BOT_TOKEN),
        developerName = environment.get(DISCORD_DEVELOPER_NAME),
        commandPrefix = "/"
    )

    suspend fun start() {
        logger.info { "Starting Discord bot" }
        kord = Kord(botSettings.token)

        kord.on<MessageCreateEvent>(scope = postingScope) {
            if (message.author?.isBot == true) return@on
            handleEvent(this@on)
        }

        kord.on<ChatInputCommandInteractionCreateEvent>(scope = postingScope) {
            interaction.deferPublicResponse().delete()
            handleEvent(this@on)
        }

        registerCommands()

        postingScope.launch {
            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
        logger.info { "Discord bot started" }
    }

    suspend fun stop() {
        logger.info { "Stopping Discord bot" }
        kord.logout()
    }

    fun observePostings(): Flow<Post> = postingsFlow

    suspend fun post(post: Post) {
        val snowflake = Snowflake(post.chat.id)

        val request = UserMessageCreateBuilder().apply {
            content = post.extra.text
            attachments = post.extra.mapAttachments(snowflake)
            allowedMentions = AllowedMentionsBuilder().apply {
                add(AllowedMentionType.UserMentions)
            }
        }.toRequest()

        kord.rest.channel.createMessage(snowflake, request)
    }

    private fun Post.Extra.mapAttachments(snowflake: Snowflake): MutableList<AttachmentBuilder> {
        val attachment = when (this) {
            is Post.Extra.Video -> AttachmentBuilder(snowflake).apply { filename = file.name }

            else -> null
        }
        return attachment?.let { mutableListOf(it) } ?: mutableListOf()
    }

    private suspend fun handleEvent(event: Event) {
        logger.info { "Received Discord event: $event" }
        val post = postingMapper.map(event, botSettings)
        postingsFlow.emit(post)
    }

    private fun registerCommands() {
        commandsRepository.getCommands().forEach { command ->
            postingScope.launch {
                kord.createGlobalChatInputCommand(
                    name = command.command,
                    description = command.description,
                ) {
                    if (command.arguments.isNotEmpty()) {
                        command.arguments.forEach { arg ->
                            string(arg.name, arg.description)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val DISCORD_DEVELOPER_NAME = "DISCORD_DEVELOPER_NAME"
        const val DISCORD_BOT_ID = "DISCORD_BOT_ID"
        const val DISCORD_BOT_NAME = "DISCORD_BOT_NAME"
        const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
    }
}
