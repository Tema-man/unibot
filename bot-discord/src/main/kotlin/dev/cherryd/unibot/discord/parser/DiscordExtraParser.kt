package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Posting
import dev.kord.core.event.Event

interface DiscordExtraParser {
    fun parse(event: Event): Posting.Content.Extra?
}