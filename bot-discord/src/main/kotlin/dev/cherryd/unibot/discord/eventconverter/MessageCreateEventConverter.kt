package dev.cherryd.unibot.discord.eventconverter

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.ChatRepository
import dev.cherryd.unibot.core.User
import dev.cherryd.unibot.discord.toChat
import dev.cherryd.unibot.discord.toUser
import dev.kord.core.cache.data.MessageData
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent

class MessageCreateEventConverter(
    private val chatRepository: ChatRepository
) : EventConverter {

    override fun toMessageId(event: Event): String = event.checkType { message.id.value.toString() }

    override fun toUser(event: Event): User = event.checkType {
        message.author?.toUser() ?: throw IllegalArgumentException("Message author is null")
    }

    override fun toChat(event: Event): Chat = event.checkType {
        val chatId = this.message.channel.id.value.toString()
        val settings = chatRepository.getSettingsForChatById(chatId)
        message.channel.toChat(settings)
    }

    override fun getReferencedMessage(event: Event): MessageData? = event.checkType {
        message.referencedMessage?.data
    }

    private inline fun <T> Event.checkType(block: MessageCreateEvent.() -> T): T {
        if (this is MessageCreateEvent) return block(this) else throw IllegalArgumentException("Event is not a MessageCreateEvent")
    }
}