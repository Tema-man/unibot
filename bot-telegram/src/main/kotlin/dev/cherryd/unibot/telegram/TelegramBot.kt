package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.ChatRepository
import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.interceptor.BotInteractor
import dev.cherryd.unibot.telegram.parser.CommandExtraParser
import dev.cherryd.unibot.telegram.parser.StickerExtraParser
import dev.cherryd.unibot.telegram.parser.UrlsExtraParser
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

class TelegramBot(
    environment: Environment,
    private val chatRepository: ChatRepository
) : TelegramLongPollingBot(environment.get(TELEGRAM_API_KEY)), BotInteractor {

    private val logger = KotlinLogging.logger("TelegramBot")
    private val tgBotApi = TelegramBotsApi(DefaultBotSession::class.java)
    private var session: BotSession? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val postingsFlow = MutableSharedFlow<Post>()

    private val postingMediaMapper = PostingMediaMapper(
        parsers = listOf(
            CommandExtraParser(),
            UrlsExtraParser(),
            StickerExtraParser()
        ),
        chatRepository = chatRepository
    )

    private val botSettings = Settings(
        id = environment.get(TELEGRAM_BOT_ID),
        name = environment.get(TELEGRAM_BOT_NAME),
        aliases = environment.getBotNameAliases(),
        token = environment.get(TELEGRAM_API_KEY),
        developerName = environment.get(TELEGRAM_DEVELOPER_NAME),
        commandPrefix = "/"
    )

    fun start() {
        logger.info { "Starting Telegram bot." }
        session?.stop()
        session = tgBotApi.registerBot(this)
        logger.info { "Telegram bot started." }
    }

    fun stop() {
        logger.info { "Stopping Telegram bot. It might take some time." }
        if (session?.isRunning == true) session?.stop()
        coroutineScope.cancel()
    }

    override fun getBotUsername(): String = botSettings.name

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return
        coroutineScope.launch {
            logger.info { "Received Telegram Update: ${update.message.text}" }
            val post = postingMediaMapper.map(update, botSettings)
            postingsFlow.emit(post)
        }
    }

    fun observePostings(): Flow<Post> = postingsFlow

    private companion object {
        const val TELEGRAM_API_KEY = "TELEGRAM_API_KEY"
        const val TELEGRAM_BOT_ID = "TELEGRAM_BOT_ID"
        const val TELEGRAM_BOT_NAME = "TELEGRAM_BOT_NAME"
        const val TELEGRAM_DEVELOPER_NAME = "TELEGRAM_DEVELOPER_NAME"
    }
}