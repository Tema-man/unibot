package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.Relay
import dev.cherryd.unibot.discord.DiscordBot
import dev.cherryd.unibot.discord.DiscordRelay
import dev.cherryd.unibot.discord.PostingMapper
import dev.cherryd.unibot.discord.parser.CommandExtraParser
import dev.cherryd.unibot.telegram.TelegramBot
import dev.cherryd.unibot.telegram.TelegramRelay

object RelaysModule {

    private val telegramBot = TelegramBot(
        environment = AppModule.environment,
        settingsRepository = RepositoriesModule.settingsRepository
    )

    private val telegramRelay = TelegramRelay(
        environment = AppModule.environment,
        commmandsRepository = RepositoriesModule.commandsRepository,
        tgBot = telegramBot
    )

    private val discordPostingMapper = PostingMapper(
        parsers = listOf(CommandExtraParser()),
        settingsRepository = RepositoriesModule.settingsRepository
    )

    private val discordBot = DiscordBot(
        AppModule.environment,
        postingMapper = discordPostingMapper
    )

    private val discordRelay = DiscordRelay(
        discordBot = discordBot
    )

    fun provideRelays(): List<Relay> = listOf(
        telegramRelay,
        discordRelay
    )
}