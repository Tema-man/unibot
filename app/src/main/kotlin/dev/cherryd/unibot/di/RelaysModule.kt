package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.Relay
import dev.cherryd.unibot.discord.DiscordRelay
import dev.cherryd.unibot.telegram.TelegramRelay

object RelaysModule {

    private val telegramRelay = TelegramRelay(
        AppModule.environment,
        RepositoriesModule.commandsRepository
    )

    private val discordRelay = DiscordRelay(
        AppModule.environment,
        RepositoriesModule.commandsRepository
    )

    fun provideRelays(): List<Relay> = listOf(
        telegramRelay,
        discordRelay
    )
}