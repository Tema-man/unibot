package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Post
import dev.kord.core.event.Event

interface DiscordExtraParser {
    fun parse(event: Event): Post.Extra?
}