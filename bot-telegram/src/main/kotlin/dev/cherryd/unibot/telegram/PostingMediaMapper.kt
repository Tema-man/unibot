package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.telegram.parser.ExtraParser
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class PostingMediaMapper(
    private val parsers: List<ExtraParser>,
    private val chatRepository: ChatRepository
) {
    fun map(update: Update, settings: Settings): Post {
        val chatSettings = chatRepository.getSettingsForChatById(update.getTgChat().id.toString())
        return mapMessage(
            message = update.message,
            sender = update.toUser(settings),
            chat = update.getUniBotChat(chatSettings),
            extra = parseExtra(update, settings),
            settings = settings
        )
    }

    private fun mapMessage(
        message: Message?,
        sender: User,
        chat: Chat,
        extra: Post.Extra,
        settings: Settings,
    ): Post = Post(
        id = message?.messageId.toString(),
        sender = sender,
        chat = chat,
        extra = extra,
        reply = parseReply(message, chat, settings),
        settings = settings
    )

    private fun parseExtra(update: Update, settings: Settings) =
        parsers.firstNotNullOfOrNull { it.parse(update, settings) } ?: Post.Extra.Text(update.text)

    private fun parseReply(
        message: Message?,
        chat: Chat,
        settings: Settings,
    ): Post? {
        val replyMessage = message?.replyToMessage ?: return null
        return mapMessage(
            message = replyMessage,
            sender = replyMessage.from.toUser(settings),
            chat = chat,
            extra = Post.Extra.Text(message.text),
            settings = settings
        )
    }
}