package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class PostingMediaMapper {
    fun map(update: Update, settings: Settings): Posting.Content {
        return mapMessage(update, settings, update.message)
    }

    private fun mapMessage(update: Update, settings: Settings, message: Message): Posting.Content {
        return Posting.Content(
            id = message.messageId.toString(),
            sender = update.toUser(),
            chat = update.getUniBotChat(),
            extra = parseExtra(update, settings),
            attachment = parseAttachment(update, settings)
        )
    }

    private fun parseExtra(update: Update, settings: Settings): Posting.Content.Extra {
        return parseCommand(update, settings)
            ?: Posting.Content.Extra.Text(update.message.text ?: "")
    }

    private fun parseCommand(update: Update, settings: Settings): Posting.Content.Extra.Command? {
        if (!update.message.isCommand) return null
        val command = update.toCommand(settings.bot.name) ?: return null

        return Posting.Content.Extra.Command(
            text = update.message.text ?: "",
            command = command
        )
    }

    private fun parseAttachment(update: Update, settings: Settings): Posting.Content.Attachment? {
       return parseReply(update, settings)
            ?: parseSticker(update, settings)
    }

    private fun parseReply(update: Update, settings: Settings): Posting.Content.Attachment.Reply? {
        val replyToMessage = update.message.replyToMessage ?: return null
        return Posting.Content.Attachment.Reply(
            replyContent = mapMessage(update, settings, replyToMessage)
        )
    }

    private fun parseSticker(update: Update, settings: Settings): Posting.Content.Attachment.Sticker? {
        val sticker = update.message.sticker ?: return null
        return Posting.Content.Attachment.Sticker(
            stickerId = sticker.fileId
        )
    }
}