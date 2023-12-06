package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import dev.cherryd.unibot.core.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow

class TelegramRelay(
    environment: Environment,
    settingsRepository: SettingsRepository
) : Relay {

    private val tgBot = TelegramBot(
        environment,
        settingsRepository
    )

    private val postingSender = PostingSender(tgBot)

    override fun incomingPostingsFlow(): Flow<Posting> = tgBot.observePostings()

    override suspend fun post(posting: Posting) {
        postingSender.send(posting)
    }

    override fun start() {
        tgBot.start()
    }

    override fun stop() {
        tgBot.stop()
    }

    override fun restart() {
        tgBot.stop()
        tgBot.start()
    }

}