package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Post

class MessagesRepository(
    private val database: Database
) {

    fun savePosting(post: Post) {
        if (post.extra !is Post.Extra.Text) return
        if (post.extra.text.length !in (5..50)) return

        database.execute(
            """
                INSERT INTO messages (chat_id, user_id, message) VALUES (?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET message = excluded.message
            """.trimIndent()
        ) {
            setString(1, post.chat.id)
            setString(2, post.sender.id)
            setString(3, post.extra.text)
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