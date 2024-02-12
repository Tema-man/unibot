package dev.cherryd.unibot.core

class Environment {
    fun get(key: String): String = runCatching { System.getenv(key) }.getOrDefault("")
}