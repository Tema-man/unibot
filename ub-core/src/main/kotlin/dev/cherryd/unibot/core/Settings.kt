package dev.cherryd.unibot.core

data class Settings(
    val developerName: String,
    val bot: Bot
) {
    data class Bot(
        val id: String,
        val name: String,
        val aliases: List<String>,
        val token: String,
        val commandPrefix: String
    )
}