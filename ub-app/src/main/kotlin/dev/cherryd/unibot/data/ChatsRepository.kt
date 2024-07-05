package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Chat

class ChatsRepository(
    private val database: Database
) {

    fun saveChat(chat: Chat) {
        database.execute(
            """
                INSERT INTO chats (id, name) VALUES (?, ?) 
                ON CONFLICT(id) DO UPDATE SET name = excluded.name, active = TRUE
            """.trimIndent()
        ) {
            setString(1, chat.id)
            setString(2, chat.name)
            executeUpdate()
        }
    }
}