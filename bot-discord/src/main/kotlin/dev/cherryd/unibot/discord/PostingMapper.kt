package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Post
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

    fun map(event: Event, settings: Settings): Post {
        val converter = eventConverters[event::class]
            ?: throw IllegalArgumentException("Unsupported event type: $event")

        val chat = converter.toChat(event)
        return Post(
            id = converter.toMessageId(event),
            sender = converter.toUser(event),
            chat = chat,
            extra = event.parseExtra(),
            reply = parseReply(converter.getReferencedMessage(event), chat, settings),
            settings = settings
        )
    }

    private fun Event.parseExtra() = parsers.firstNotNullOfOrNull { it.parse(this) } ?: Post.Extra.Text("")

    private fun parseReply(
        referencedMessage: MessageData?,
        chat: Chat,
        settings: Settings
    ): Post? {
        if (referencedMessage == null) return null

        return Post(
            id = referencedMessage.id.toString(),
            sender = referencedMessage.author.toUser(),
            chat = chat,
            extra = Post.Extra.Text(referencedMessage.content),
            reply = null,
            settings = settings
        )
    }
}
