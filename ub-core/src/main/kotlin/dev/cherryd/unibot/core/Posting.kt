package dev.cherryd.unibot.core

import java.io.File

data class Posting(
    val content: Content,
    val settings: Settings
) {

    val extra: Content.Extra get() = content.extra

    fun answer(extra: Content.Extra) = copy(content = content.copy(extra = extra))

    data class Content(
        val id: String,
        val sender: User,
        val chat: Chat,
        val extra: Extra,
        val reply: Content? = null,
    ) {
        sealed class Extra {
            data class Text(val text: String) : Extra()
            data class Command(val command: String, val text: String) : Extra()
            data class Urls(val urls: List<String>, val text: String) : Extra()
            data class Composite(val content: List<Extra>) : Extra()
            data class Video(val file: File) : Extra()
            data class Sticker(val stickerId: String) : Extra()
            data class Reaction(val emoji: String) : Extra()
            sealed class ChatEvent : Extra() {
                data class UserJoined(val user: User) : ChatEvent()
                data class UserLeft(val user: User) : ChatEvent()
                data object BotAdded : ChatEvent()
                data object BotRemoved : ChatEvent()
            }
        }
    }
}
