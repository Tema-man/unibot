package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.discord.eventconverter.EventConverter
import dev.cherryd.unibot.discord.eventconverter.MessageCreateEventConverter
import dev.cherryd.unibot.discord.parser.DiscordExtraParser
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

        return Posting.Content(
            id = converter.toMessageId(event),
            sender = converter.toUser(event),
            chat = converter.toChat(event),
            extra = event.parseExtra(),
            reply = parseReply()
        )
    }

    private fun Event.parseExtra() = parsers.firstNotNullOfOrNull { it.parse(this) } ?: Posting.Content.Extra.Text("")

    private fun parseReply(): Posting.Content? {
        return null
    }
}
