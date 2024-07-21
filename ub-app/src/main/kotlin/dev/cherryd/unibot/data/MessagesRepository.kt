package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Post
import io.github.oshai.kotlinlogging.KotlinLogging

class MessagesRepository(
    private val database: Database
) {

    private val logger = KotlinLogging.logger {}

    fun savePosting(post: Post) {
        logger.debug { "Saving posting: ${post}" }
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
            logger.info { "Posting saved" }
        }
    }

    fun getRandomPosting(): String {
        logger.debug { "Getting random posting" }
        val posting = database.execute(
            """
            SELECT message FROM messages ORDER BY RANDOM() LIMIT 1
        """.trimIndent()
        ) {
            executeQuery().use { resultSet ->
                if (!resultSet.next()) return@execute ""
                resultSet.getString("message")
            }
        } ?: ""
        logger.debug { "Selected random posting: $posting" }
        return posting
    }

}