package dev.cherryd.unibot.core

class Environment {
    fun get(key: String): String = runCatching { System.getenv(key) }.getOrDefault("")

    fun getBotNameAliases(): List<String> = get("BOT_NAME_ALIASES").split(",").map { it.trim() }
}