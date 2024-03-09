package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Posting
import dev.kord.core.entity.Message

interface DiscordExtraParser {
    fun parse(message: Message): Posting.Content.Extra?
}