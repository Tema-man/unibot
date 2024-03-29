package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.CommandsRepository
import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import dev.cherryd.unibot.discord.parser.CommandExtraParser
import kotlinx.coroutines.flow.Flow

class DiscordRelay(
    private val environment: Environment,
    private val commmandsRepository: CommandsRepository
) : Relay {

    private val discordBot = DiscordBot(
        environment,
        postingMapper = PostingMapper(
            listOf(CommandExtraParser())
        )
    )

    override fun incomingPostingsFlow(): Flow<Posting> = discordBot.observePostings()

    override suspend fun post(posting: Posting) {
        discordBot.post(posting)
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