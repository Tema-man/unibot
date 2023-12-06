package dev.cherryd.unibot.core

import dev.cherryd.unibot.core.settings.Settings

data class Context(
    val chat: Chat,
    val from: User,
    val settings: Settings,
    val posting: Posting,
)