package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Posting
import dev.kord.core.entity.Message

class CommandExtraParser : DiscordExtraParser {
    override fun parse(message: Message): Posting.Content.Extra? {
        val content = message.content
        if (!content.startsWith("!")) return null
        val command = content.substringBefore(" ")
        return Posting.Content.Extra.Command(command, content)
    }
}