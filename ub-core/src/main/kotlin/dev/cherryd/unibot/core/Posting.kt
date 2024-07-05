package dev.cherryd.unibot.core

import java.io.File

data class Posting(
    val content: Content,
    val settings: Settings
) {

    val extra: Content.Extra get() = content.extra

    fun answer(
        extra: Content.Extra,
        reply: Boolean = false
    ) = copy(
        content = content.copy(
            extra = extra,
            reply = content.takeIf { reply }
        ),
    )

    inline fun textAnswer(
        reply: Boolean = false,
        text: () -> String
    ) = answer(Content.Extra.Text(text()), reply)

    data class Content(
        val id: String,
        val sender: User,
        val chat: Chat,
        val extra: Extra,
        val reply: Content? = null,
    ) {
        sealed class Extra(
            open val text: String
        ) {
            data class Text(override val text: String) : Extra(text)
            data class Command(val command: String, override val text: String) : Extra(text)
            data class Urls(val urls: List<String>, override val text: String) : Extra(text)
            data class Video(val file: File) : Extra("")
            data class Sticker(val stickerId: String) : Extra("")
            data class Reaction(val emoji: String) : Extra("")
            sealed class ChatEvent : Extra("") {
                data class UserJoined(val user: User) : ChatEvent()
                data class UserLeft(val user: User) : ChatEvent()
                data object BotAdded : ChatEvent()
                data object BotRemoved : ChatEvent()
                data object SendingVideo : ChatEvent()
            }
        }
    }
}