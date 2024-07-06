package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Post
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent

class CommandExtraParser : DiscordExtraParser {
    override fun parse(event: Event): Post.Extra? {
        val message = (event as? MessageCreateEvent)?.message ?: return null
        val content = message.content
        if (!content.startsWith("!")) return null
        val command = content.substringBefore(" ").removePrefix("!")
        return Post.Extra.Command(command, content)
    }
}