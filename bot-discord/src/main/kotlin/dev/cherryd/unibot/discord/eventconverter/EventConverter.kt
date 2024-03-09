package dev.cherryd.unibot.discord.eventconverter

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import dev.kord.core.event.Event

interface EventConverter {
    fun toMessageId(event: Event): String
    fun toUser(event: Event): User
    fun toChat(event: Event): Chat
}