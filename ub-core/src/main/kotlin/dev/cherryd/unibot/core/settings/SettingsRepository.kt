package dev.cherryd.unibot.core.settings

interface SettingsRepository {
    suspend fun getSettings(
        userId: String,
        chatId: String,
        botName: String
    ): Settings
}