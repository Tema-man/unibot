package dev.cherryd.unibot.core

data class User(
    val id: String,
    val name: String,
    val role: Role
) {
    enum class Role {
        DEVELOPER, USER, ADMIN, BOT
    }
}
