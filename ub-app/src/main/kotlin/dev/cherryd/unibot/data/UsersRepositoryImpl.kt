package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import dev.cherryd.unibot.core.UsersRepository
import io.github.oshai.kotlinlogging.KotlinLogging

class UsersRepositoryImpl(
    private val database: Database
) : UsersRepository {

    private val logger = KotlinLogging.logger {}

    override fun saveUser(user: User) {
        logger.debug { "Saving user $user" }
        database.execute(
            """
                INSERT INTO users (id, name, mention, role) VALUES (?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET 
                    name = excluded.name,
                    mention = excluded.mention,
                    role = excluded.role
            """.trimIndent()
        ) {
            setString(1, user.id)
            setString(2, user.name)
            setString(3, user.mention)
            setString(4, user.role.name.lowercase())
            val result = executeUpdate()
            logger.debug { "User $user saved, result: $result" }
        }
    }

    override fun saveUsers(users: List<User>) {
        logger.debug { "Saving users list $users" }
        database.execute(
            """
                INSERT INTO users (id, name, mention, role) VALUES (?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET name = excluded.name, role = excluded.role
            """.trimIndent()
        ) {
            users.forEachIndexed { index, user ->
                setString(1, user.id)
                setString(2, user.name)
                setString(3, user.mention)
                setString(3, user.role.name.lowercase())
                addBatch()
            }
            val result = executeBatch()
            logger.debug { "Users list $users saved, result: $result" }
        }
    }

    override fun linkUserToChat(user: User, chat: Chat) {
        logger.debug { "Linking user $user to chat $chat" }
        database.execute(
            """
                INSERT INTO users2chats (user_id, chat_id) VALUES (?, ?) 
                ON CONFLICT DO NOTHING
            """.trimIndent()
        ) {
            setString(1, user.id)
            setString(2, chat.id)
            val result = executeUpdate()
            logger.debug { "User $user linked to chat $chat, result: $result" }
        }
    }

    override fun getUsersOfChat(chat: Chat, activeOnly: Boolean): List<User> {
        logger.debug { "Getting ${if (activeOnly) "active only " else ""}users of chat $chat" }
        return database.execute(
            """
                SELECT u.id, u.name, u.mention, u.role
                FROM users u
                JOIN users2chats u2c ON u.id = u2c.user_id
                WHERE u2c.chat_id = ?
                ${if (activeOnly) "AND u2c.active = TRUE" else ""}
            """.trimIndent()
        ) {
            setString(1, chat.id)
            executeQuery().use { resultSet ->
                generateSequence { if (resultSet.next()) resultSet else null }
                    .mapNotNull { User.fromResultSet(it) }
                    .toList()
            }
        }.orEmpty().also {
            logger.debug { "Got ${it.size} ${if (activeOnly) "active only " else ""}users of chat $chat" }
        }
    }

    override fun findUserByName(name: String): User? {
        logger.debug { "Finding user by name $name" }
        return database.execute(
            """
                SELECT id, name, mention, role
                FROM users
                WHERE name = ?
            """.trimIndent()
        ) {
            setString(1, name)
            executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    logger.debug { "No user found by name $name" }
                    return@execute null
                }
                User.fromResultSet(resultSet)
                    .also { logger.debug { "User $it found by name $name" } }
            }
        }
    }

    override fun findUserByMention(mention: String): User? {
        logger.debug { "Finding user by mention $mention" }
        return database.execute(
            """
                SELECT id, name, mention, role
                FROM users
                WHERE mention = ?
            """.trimIndent()
        ) {
            setString(1, mention)
            executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    logger.debug { "No user found by mention $mention" }
                    return@execute null
                }
                User.fromResultSet(resultSet)
                    .also { logger.debug { "User $it found by name $mention" } }
            }
        }
    }
}