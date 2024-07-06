package dev.cherryd.unibot.core

import java.io.File

data class Post(
    val id: String,
    val sender: User,
    val chat: Chat,
    val extra: Extra,
    val reply: Post? = null,
    val settings: Settings
) {

    fun answer(
        extra: Extra,
        reply: Boolean = false
    ) = copy(
        extra = extra,
        reply = this.takeIf { reply }
    )

    inline fun textAnswer(
        reply: Boolean = false,
        text: () -> String
    ) = answer(Extra.Text(text()), reply)

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