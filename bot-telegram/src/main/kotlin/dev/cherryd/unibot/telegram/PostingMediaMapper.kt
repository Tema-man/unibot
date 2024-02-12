package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.telegram.parser.ExtraParser
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class PostingMediaMapper(
    private val parsers: List<ExtraParser>
) {
    fun map(update: Update, settings: Settings): Posting.Content {
        return mapMessage(update, settings, update.message)
    }

    private fun mapMessage(update: Update, settings: Settings, message: Message?): Posting.Content {
        return Posting.Content(
            id = message?.messageId.toString(),
            sender = update.toUser(settings),
            chat = update.getUniBotChat(),
            extra = parseExtra(update, settings),
            reply = parseReply(update, settings)
        )
    }

    private fun parseExtra(update: Update, settings: Settings) =
        parsers.firstNotNullOfOrNull { it.parse(update, settings) }
            ?: Posting.Content.Extra.Text(update.text)

    private fun parseReply(update: Update, settings: Settings): Posting.Content? {
        val replyToMessage = update.message.replyToMessage ?: return null
        return mapMessage(update, settings, replyToMessage)
    }
}