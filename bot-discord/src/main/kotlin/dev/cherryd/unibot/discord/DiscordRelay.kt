package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Relay
import kotlinx.coroutines.flow.Flow

class DiscordRelay(
    private val discordBot: DiscordBot
) : Relay {

    override val interactor = discordBot

    override fun incomingPostingsFlow(): Flow<Post> = discordBot.observePostings()

    override suspend fun post(post: Post) {
        discordBot.post(post)
    }

    override suspend fun start() {
        discordBot.start()
    }

    override suspend fun afterStartSetup() {

    }

    override suspend fun stop() {
        discordBot.stop()
    }

    override suspend fun restart() {
        stop()
        start()
    }
}