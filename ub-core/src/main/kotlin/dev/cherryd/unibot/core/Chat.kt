package dev.cherryd.unibot.core

data class Chat(
    val id: String,
    val name: String,
    val type: Type
) {

    val isGroup: Boolean
        get() = type == Type.GROUP || type == Type.SUPERGROUP

    enum class Type {
        PRIVATE, GROUP, SUPERGROUP
    }
}
