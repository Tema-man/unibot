package dev.cherryd.unibot.discord.eventconverter

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.ChatRepository
import dev.cherryd.unibot.core.User
import dev.cherryd.unibot.discord.toChat
import dev.cherryd.unibot.discord.toUser
import dev.kord.core.cache.data.MessageData
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

class CommandReceivedEventConverter(
    private val chatRepository: ChatRepository
) : EventConverter {
    override fun toMessageId(event: Event): String = event.checkType {
        interaction.command.data.id.value?.value.toString()
    }

    override fun toUser(event: Event): User = event.checkType {
        interaction.user.toUser()
    }

    override fun toChat(event: Event): Chat = event.checkType {
        val chatId = interaction.channel.id.value.toString()
        val settings = chatRepository.getSettingsForChatById(chatId)
        interaction.channel.toChat(settings)
    }

    override fun getReferencedMessage(event: Event): MessageData? = null // No referenced message in command interaction

    private inline fun <T> Event.checkType(block: ChatInputCommandInteractionCreateEvent.() -> T): T {
        if (this is ChatInputCommandInteractionCreateEvent) return block(this)
        else throw IllegalArgumentException("Event is not a MessageCreateEvent")
    }
}