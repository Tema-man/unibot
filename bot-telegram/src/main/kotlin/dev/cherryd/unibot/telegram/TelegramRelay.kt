package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import kotlinx.coroutines.flow.Flow

class TelegramRelay(
    environment: Environment,
) : Relay {

    private val tgBot = TelegramBot(environment)

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