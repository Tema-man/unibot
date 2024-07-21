package dev.cherryd.unibot.core

import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.ResultSet

data class User(
    val id: String,
    val name: String,
    val role: Role
) {
    enum class Role {
        DEVELOPER, USER, ADMIN, BOT;

        companion object {
            fun fromString(role: String): Role = entries.find { it.name.equals(role, ignoreCase = true) } ?: USER
        }
    }

    companion object Builder {
        private val logger = KotlinLogging.logger {}

        fun fromResultSet(resultSet: ResultSet): User? = kotlin.runCatching {
            User(
                id = resultSet.getString("id"),
                name = resultSet.getString("name"),
                role = Role.fromString(resultSet.getString("role"))
            )
        }.onFailure { logger.error(it) { "Unable to restore User from DB record" } }.getOrNull()
    }
}
