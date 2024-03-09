package dev.cherryd.unibot.discord.eventconverter

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import dev.cherryd.unibot.discord.toChat
import dev.cherryd.unibot.discord.toUser
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent

class MessageCreateEventConverter : EventConverter {

    override fun toMessageId(event: Event): String = event.checkType { message.id.value.toString() }

    override fun toUser(event: Event): User = event.checkType {
        message.author?.toUser() ?: throw IllegalArgumentException("Message author is null")
    }

    override fun toChat(event: Event): Chat = event.checkType { message.channel.toChat() }

    private inline fun <T> Event.checkType(block: MessageCreateEvent.() -> T): T {
        if (this is MessageCreateEvent) return block(this) else throw IllegalArgumentException("Event is not a MessageCreateEvent")
    }
}