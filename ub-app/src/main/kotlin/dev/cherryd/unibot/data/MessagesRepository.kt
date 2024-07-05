package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Posting

class MessagesRepository(
    private val database: Database
) {

    fun savePosting(posting: Posting) {
        database.execute(
            """
                INSERT INTO messages (chat_id, user_id, message) VALUES (?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET message = excluded.message
            """.trimIndent()
        ) {
            setString(1, posting.content.chat.id)
            setString(2, posting.content.sender.id)
            setString(3, posting.content.extra.text)
            executeUpdate()
        }
    }

    fun getRandomPosting(): String = database.execute(
        """
            SELECT message FROM messages ORDER BY RANDOM() LIMIT 1
        """.trimIndent()
    ) {
        executeQuery().use { resultSet ->
            if (!resultSet.next()) return@execute ""
            resultSet.getString("message")
        }
    } ?: ""

}