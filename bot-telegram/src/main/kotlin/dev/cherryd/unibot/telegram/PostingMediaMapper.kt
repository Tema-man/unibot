package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.User
import dev.cherryd.unibot.telegram.parser.ExtraParser
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class PostingMediaMapper(
    private val parsers: List<ExtraParser>
) {
    fun map(update: Update, settings: Settings): Posting.Content = mapMessage(
        message = update.message,
        sender = update.toUser(settings),
        chat = update.getUniBotChat(),
        extra = parseExtra(update, settings),
        settings = settings
    )

    private fun mapMessage(
        message: Message?,
        sender: User,
        chat: Chat,
        extra: Posting.Content.Extra,
        settings: Settings,
    ): Posting.Content = Posting.Content(
        id = message?.messageId.toString(),
        sender = sender,
        chat = chat,
        extra = extra,
        reply = parseReply(message, chat, settings)
    )

    private fun parseExtra(update: Update, settings: Settings) =
        parsers.firstNotNullOfOrNull { it.parse(update, settings) }
            ?: Posting.Content.Extra.Text(update.text)

    private fun parseReply(
        message: Message?,
        chat: Chat,
        settings: Settings,
    ): Posting.Content? {
        val replyMessage = message?.replyToMessage ?: return null
        return mapMessage(
            message = replyMessage,
            sender = replyMessage.from.toUser(settings),
            chat = chat,
            extra = Posting.Content.Extra.Text(message.text),
            settings = settings
        )
    }
}