@file:UseSerializers(LocalDateSerializer::class)

package dev.cherryd.unibot.core

import dev.cherryd.unibot.core.serializers.LocalDateSerializer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import java.sql.ResultSet
import java.time.LocalDate

data class Chat(
    val id: String,
    val name: String,
    val type: Type,
    val settings: Settings
) {

    val isGroup: Boolean
        get() = type == Type.GROUP || type == Type.SUPERGROUP

    @Serializable
    data class Settings(
        val lastSyncDate: LocalDate?,
        val randomThresholdProbability: Float
    ) {
        companion object {
            val DEFAULT = Settings(
                lastSyncDate = null,
                randomThresholdProbability = 0.2f
            )
        }
    }

    enum class Type {
        PRIVATE, GROUP, SUPERGROUP
    }

    companion object Factory {
        private val logger = KotlinLogging.logger {}

        fun fromResultSet(resultSet: ResultSet): Chat? = kotlin.runCatching {
            Chat(
                id = resultSet.getString("id"),
                name = resultSet.getString("name"),
                type = Type.valueOf(resultSet.getString("type")),
                settings = Json.decodeFromString(resultSet.getString("settings"))
            )
        }.onFailure { logger.error(it) { "Unable to restore User from DB record" } }.getOrNull()
    }
}
