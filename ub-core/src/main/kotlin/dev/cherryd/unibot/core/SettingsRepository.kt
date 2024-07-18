package dev.cherryd.unibot.core

interface SettingsRepository {
    fun saveChatSettings(chat: Chat, settings: Settings.Chat)
    fun getChatSettings(chat: Chat): Settings.Chat
}