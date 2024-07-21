package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.telegram.text
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Update

class CommandExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Post.Extra? {
        if (update.message?.isCommand == false) return null
        val command = update.toCommand(settings.name) ?: return null
        return Post.Extra.Command(command = command.removePrefix("/"), text = update.text)
    }

    private fun Update.toCommand(botName: String): String? {
        val entities = message?.entities ?: return null
        for (entity in entities) {
            if (entity.offset != 0 || entity.type != EntityType.BOTCOMMAND) continue
            val parts = entity.text.split("@")
            if (parts.size == 1) return parts[0]
            if (parts.getOrNull(1) == botName) return parts[0]
        }
        return null
    }
}