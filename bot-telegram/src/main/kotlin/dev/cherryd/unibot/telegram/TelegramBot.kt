package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.settings.SettingsRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

internal class TelegramBot(
    private val environment: Environment,
    private val settingsRepository: SettingsRepository
) : TelegramLongPollingBot(environment.get(TELEGRAM_API_KEY)) {

    private val logger = KotlinLogging.logger("TelegramBot")
    private val tgBotApi = TelegramBotsApi(DefaultBotSession::class.java)
    private var session: BotSession? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val postingsFlow = MutableSharedFlow<Posting>()

    private val postingMediaMapper = PostingMediaMapper()

    fun start() {
        session?.stop()
        session = tgBotApi.registerBot(this)
    }

    fun stop() {
        logger.info { "Stopping Telegram bot. It might take some time." }
        session?.stop()
    }

    override fun getBotUsername(): String = environment.get(TELEGRAM_BOT_NAME)

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return
        coroutineScope.launch {
            logger.info { "Received Telegram Update: ${update.message.text}" }

            val chat = update.getUniBotChat()
            val from = update.toUser()
            val settings = settingsRepository.getSettings(
                userId = from.id,
                chatId = chat.id,
                botName = botUsername
            )

            val media = postingMediaMapper.map(update, settings)

            val posting = Posting(
                id = update.updateId.toString(),
                from = from,
                chat = chat,
                media = media,
                reply = null,
                settings = settings
            )

            postingsFlow.emit(posting)
        }
    }

    fun observePostings(): Flow<Posting> = postingsFlow

    private companion object {
        const val TELEGRAM_API_KEY = "TELEGRAM_API_KEY"
        const val TELEGRAM_BOT_NAME = "TELEGRAM_BOT_NAME"
    }
}