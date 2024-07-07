package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Date
import java.time.LocalDate

class PidorsRepository(
    private val database: Database
) {

    private val logger = KotlinLogging.logger {}

    fun savePidor(user: User, chat: Chat, date: LocalDate = LocalDate.now()) {
        logger.debug { "Saving pidor $user in chat $chat on $date" }
        database.execute(
            """
                INSERT INTO pidors (user_id, chat_id, date) VALUES (?, ?, ?) 
                ON CONFLICT DO NOTHING
            """.trimIndent()
        ) {
            setString(1, user.id)
            setString(2, chat.id)
            setDate(3, Date.valueOf(date))
            executeUpdate()
            logger.debug { "Pidor $user saved in chat $chat on $date" }
        }
    }

    fun getPidorOfChat(chat: Chat, date: LocalDate = LocalDate.now()): User? {
        logger.debug { "Getting pidor of chat $chat on $date" }
        return database.execute(
            """
                SELECT u.id, u.name, u.role
                FROM pidors p
                JOIN users u ON p.user_id = u.id
                WHERE p.chat_id = ? AND p.date BETWEEN ? AND ?
            """.trimIndent()
        ) {
            setString(1, chat.id)
            setDate(2, Date.valueOf(date.atStartOfDay().toLocalDate()))
            setDate(3, Date.valueOf(date.plusDays(1).atStartOfDay().toLocalDate()))
            executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    logger.debug { "No pidor found in chat $chat on $date" }
                    return@execute null
                }
                User.fromResultSet(resultSet).also { logger.debug { "Pidor $it found in chat $chat on $date" } }
            }
        }
    }
}