package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.telegram.text
import dev.cherryd.unibot.telegram.toCommand
import org.telegram.telegrambots.meta.api.objects.Update

class CommandExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Posting.Content.Extra? {
        if (update.message?.isCommand == false) return null
        val command = update.toCommand(settings.bot.name) ?: return null

        return Posting.Content.Extra.Command(command = command.removePrefix("/"), text = update.text)
    }
}