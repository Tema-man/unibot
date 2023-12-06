package dev.cherryd.unibot

import dev.cherryd.unibot.core.settings.Settings
import dev.cherryd.unibot.core.settings.SettingsRepository

class SettingsRepositoryImpl(
    // potentially redis and database connections
): SettingsRepository {
    override suspend fun getSettings(userId: String, chatId: String, botName: String): Settings {
        // todo: get settings from persistence by key based on userid and chatid, parse it

        return Settings(
            botName = botName
        )
    }
}