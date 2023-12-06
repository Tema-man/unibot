package dev.cherryd.unibot.core

import dev.cherryd.unibot.core.settings.Settings

data class Posting(
    val id: String,
    val from: User,
    val chat: Chat,
    val media: Media,
    val reply: Posting?,
    val settings: Settings
) {
    sealed class Media {

        data class Text(val text: String) : Media()
        data class Command(
            val text: String,
            val command: String
        ) : Media()
    }
}
