package dev.cherryd.unibot.core

interface ChatRepository {
    fun createChat(chat: Chat)
    fun updateChat(chat: Chat)
    fun getSettingsForChatById(chatId: String): Chat.Settings
}