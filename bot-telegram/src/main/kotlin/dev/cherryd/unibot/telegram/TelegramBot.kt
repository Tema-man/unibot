package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
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

internal class TelegramBot(
    private val environment: Environment
) : TelegramLongPollingBot(environment.get(TELEGRAM_API_KEY)) {

    private val logger = KotlinLogging.logger("TelegramBot")
    private val tgBotApi = TelegramBotsApi(DefaultBotSession::class.java)
    private var session: BotSession? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val postingsFlow = MutableSharedFlow<Posting>()

    private val postingMediaMapper = PostingMediaMapper(
        parsers = listOf(
            CommandExtraParser(),
            UrlsExtraParser(),
            StickerExtraParser()
        )
    )

    private val botSettings = Settings.Bot(
        name = environment.get(TELEGRAM_BOT_NAME),
        token = environment.get(TELEGRAM_API_KEY)
    )

    fun start() {
        logger.info { "Starting Telegram bot." }
        session?.stop()
        session = tgBotApi.registerBot(this)
    }

    fun stop() {
        logger.info { "Stopping Telegram bot. It might take some time." }
        if(session?.isRunning == true) session?.stop()
        coroutineScope.cancel()
    }

    override fun getBotUsername(): String = botSettings.name

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return
        coroutineScope.launch {
            logger.info { "Received Telegram Update: ${update.message.text}" }

            val settings = Settings(
                developerName = environment.get(DEVELOPER_NAME),
                bot = botSettings
            )
            val media = postingMediaMapper.map(update, settings)

            val posting = Posting(
                settings = settings,
                content = media
            )

            postingsFlow.emit(posting)
        }
    }

    fun observePostings(): Flow<Posting> = postingsFlow

    private companion object {
        const val TELEGRAM_API_KEY = "TELEGRAM_API_KEY"
        const val TELEGRAM_BOT_NAME = "TELEGRAM_BOT_NAME"
        const val DEVELOPER_NAME = "DEVELOPER_NAME"
    }
}