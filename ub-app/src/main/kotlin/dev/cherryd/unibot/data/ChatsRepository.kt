package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Chat

class ChatsRepository(
    private val database: Database
) {

    fun saveChat(chat: Chat) {
        database.getConnection().use { connection ->
            connection.prepareStatement(
                """
                    INSERT INTO chats (id, name) VALUES (?, ?) 
                    ON CONFLICT(id) DO UPDATE SET name = excluded.name, active = TRUE
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, chat.id)
                statement.setString(2, chat.name)
                statement.executeUpdate()
            }
        }
    }
}