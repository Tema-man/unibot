package dev.cherryd.unibot

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.SettingsRepository
import dev.cherryd.unibot.data.Database
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepositoryImpl(
    private val database: Database
) : SettingsRepository {

    private val logger = KotlinLogging.logger {}

    override fun saveChatSettings(chat: Chat, settings: Settings.Chat) {
        logger.debug { "Saving chat settings for $chat: $settings" }
        database.execute(
            """
                INSERT INTO chat_settings (chat_id, settings) VALUES (?, ?) 
                ON CONFLICT(chat_id) DO UPDATE SET settings = excluded.settings
            """.trimIndent()
        ) {
            setString(1, chat.id)
            setString(2, Json.encodeToString(settings))
            val result = executeUpdate()
            logger.debug { "Chat settings for $chat saved, result: $result" }
        }
    }

    override fun getChatSettings(chat: Chat): Settings.Chat {
        logger.debug { "Getting chat settings for $chat" }
        return database.execute(
            """
                SELECT settings FROM chat_settings WHERE chat_id = ?
            """.trimIndent()
        ) {
            setString(1, chat.id)
            val resultSet = executeQuery()
            if (!resultSet.next()) return@execute null

            val string = resultSet.getString("settings")
            runCatching { Json.decodeFromString<Settings.Chat>(string) }
                .onFailure { logger.error(it) { "Failed to parse chat settings: $string" } }
                .getOrNull()

        }
            ?.also { logger.debug { "Chat settings for $chat: $it" } }
            ?: Settings.Chat.DEFAULT.also {
                logger.debug { "Chat settings for $chat: not found. Bypassing to default." }
            }
    }
}