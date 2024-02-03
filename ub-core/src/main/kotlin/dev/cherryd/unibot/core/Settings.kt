package dev.cherryd.unibot.core

data class Settings(
    val developerName: String,
    val bot: Bot
) {
    data class Bot(
        val name: String,
        val token: String
    )
}