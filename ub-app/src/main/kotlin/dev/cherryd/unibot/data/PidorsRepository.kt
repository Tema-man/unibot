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

    fun getPidorsLeaderboard(chat: Chat, limit: Int = 5): Map<User, Int> {
        logger.debug { "Getting all pidors of chat $chat" }
        return database.execute(
            """
                SELECT COUNT(*) as count, u.id, u.name, u.role
                FROM pidors p
                JOIN users u ON p.user_id = u.id
                WHERE p.chat_id = ?
                GROUP BY u.id, u.name, u.role
                ORDER BY count DESC
                LIMIT ?
            """.trimIndent()
        ) {
            setString(1, chat.id)
            setInt(2, limit)
            executeQuery().use { resultSet ->
                val pidors = buildMap {
                    while (resultSet.next()) {
                        User.fromResultSet(resultSet)?.let { put(it, resultSet.getInt("count")) }
                    }
                }
                return@execute pidors
            }
        }.orEmpty().also {
            logger.debug { "Pidors of chat $chat | $it" }
        }
    }

    fun getPidorRecordsForUser(user: User, chat: Chat): Pair<Int, LocalDate>? {
        logger.debug { "Getting pidor records for user $user in chat $chat" }
        return database.execute(
            """
                SELECT COUNT(*) as count, date
                FROM pidors
                WHERE user_id = ? AND chat_id = ?
                GROUP BY date
                ORDER BY date DESC
            """.trimIndent()
        ) {
            setString(1, user.id)
            setString(2, chat.id)
            executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return@execute null
                }
                val count = resultSet.getInt("count")
                val lastPirorDate = resultSet.getDate("date")?.toLocalDate() ?: return@execute null
                return@execute count to lastPirorDate
            }
        }.also {
            if (it != null) {
                logger.debug { "Pidor records for user $user in chat $chat: $it" }
            } else {
                logger.debug { "No pidor records found for user $user in chat $chat" }
            }
        }
    }
}