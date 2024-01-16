package dev.cherryd.unibot.core

class Environment {
    fun get(key: String): String = System.getenv(key)
}