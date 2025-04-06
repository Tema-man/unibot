package dev.cherryd.unibot.discord.parser

import dev.cherryd.unibot.core.Post
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

class CommandExtraParser : DiscordExtraParser {
    override fun parse(event: Event): Post.Extra? {
        if (event !is ChatInputCommandInteractionCreateEvent) return null

        val command = event.interaction.command.data.name.value ?: return null
        val args = event.interaction.command.data.options.value?.joinToString(" ") { option ->
            option.value.value?.value?.toString() ?: ""
        }.orEmpty()
        return Post.Extra.Command(command, text = "$command $args".trim())
    }
}