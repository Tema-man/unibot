package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Context
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.settings.Settings
import org.telegram.telegrambots.meta.api.objects.Update

class PostingMediaMapper {
    fun map(update: Update, settings: Settings): Posting.Media {
        return mapCommand(update, settings)
            ?: Posting.Media.Text(update.message.text ?: "")
    }

    private fun mapCommand(update: Update, settings: Settings): Posting.Media.Command? {
        if (!update.message.isCommand) return null
        val command = update.toCommand(settings.botName) ?: return null

        return Posting.Media.Command(
            text = update.message.text,
            command = command
        )
    }
}