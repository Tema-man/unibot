package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.discord.eventconverter.EventConverter
import dev.cherryd.unibot.discord.eventconverter.MessageCreateEventConverter
import dev.cherryd.unibot.discord.parser.DiscordExtraParser
import dev.kord.core.cache.data.MessageData
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import kotlin.reflect.KClass

class PostingMapper(
    private val parsers: List<DiscordExtraParser>
) {

    private val eventConverters = mapOf<KClass<*>, EventConverter>(
        MessageCreateEvent::class to MessageCreateEventConverter(),
    )

    fun map(event: Event, settings: Settings): Posting.Content {
        val converter = eventConverters[event::class]
            ?: throw IllegalArgumentException("Unsupported event type: $event")

        val chat = converter.toChat(event)
        return Posting.Content(
            id = converter.toMessageId(event),
            sender = converter.toUser(event),
            chat = chat,
            extra = event.parseExtra(),
            reply = parseReply(converter.getReferencedMessage(event), chat)
        )
    }

    private fun Event.parseExtra() = parsers.firstNotNullOfOrNull { it.parse(this) }
        ?: Posting.Content.Extra.Text("")

    private fun parseReply(referencedMessage: MessageData?, chat: Chat): Posting.Content? {
        if (referencedMessage == null) return null

        return Posting.Content(
            id = referencedMessage.id.toString(),
            sender = referencedMessage.author.toUser(),
            chat = chat,
            extra = Posting.Content.Extra.Text(referencedMessage.content),
            reply = null
        )
    }
}
