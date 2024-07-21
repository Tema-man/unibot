package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.ChatRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatRepositoryImpl(
    private val database: Database
) : ChatRepository {

    private val logger = KotlinLogging.logger {}

    override fun createChat(chat: Chat) {
        logger.debug { "Creating chat $chat" }
        database.execute(
            """
                INSERT INTO chats (id, name, settings) VALUES (?, ?, ?::jsonb) 
                ON CONFLICT(id) DO NOTHING
            """.trimIndent()
        ) {
            setString(1, chat.id)
            setString(2, chat.name)
            setString(3, Json.encodeToString(chat.settings))
            val result = executeUpdate()
            logger.debug {
                if (result == 0) "Chat $chat already exists" else "Chat $chat created"
            }
        }
    }

    override fun updateChat(chat: Chat) {
        logger.debug { "Saving chat $chat" }
        database.execute(
            """
                UPDATE chats 
                SET name = ?, settings = ?::jsonb 
                WHERE id = ?
            """.trimIndent()
        ) {
            setString(1, chat.name)
            setString(2, Json.encodeToString(chat.settings))
            setString(3, chat.id)
            executeUpdate()
            logger.debug { "Chat $chat saved" }
        }
    }

    override fun getSettingsForChatById(chatId: String): Chat.Settings {
        logger.debug { "Getting settings for chat with id $chatId" }
        return database.execute(
            """
                SELECT settings FROM chats WHERE id = ?
            """.trimIndent()
        ) {
            setString(1, chatId)
            executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    logger.debug { "No chat found with id $chatId" }
                    return@execute null
                }

                runCatching<Chat.Settings> {
                    Json.decodeFromString(resultSet.getString("settings"))
                }.onFailure {
                    logger.error(it) { "Unable to restore Chat.Settings from DB record" }
                }.getOrNull()
            }.also { logger.debug { "Settings for chat with id $chatId: $it" } }
        } ?: Chat.Settings.DEFAULT
    }
}