package dev.cherryd.unibot.core

data class Settings(
    val bot: Bot
) {
    data class Bot(
        val name: String,
        val token: String
    )
}