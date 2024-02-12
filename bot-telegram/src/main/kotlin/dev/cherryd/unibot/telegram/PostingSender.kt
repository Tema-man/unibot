package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Posting
import kotlinx.coroutines.delay
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ln

class PostingSender(
    private val tgSender: AbsSender
) {

    suspend fun send(posting: Posting) {
        when (val extra = posting.extra) {
            is Posting.Content.Extra.Text -> sendText(posting, extra.text)
            is Posting.Content.Extra.Video -> sendVideo(posting, extra.file)
            is Posting.Content.Extra.ChatEvent.SendingVideo -> {
                tgSender.execute(SendChatAction(posting.content.chat.id, "upload_video", null))
            }
            else -> {}
        }
    }

    private fun sendVideo(posting: Posting, file: File) {
        val video = SendVideo
            .builder()
            .video(InputFile(file))
            .chatId(posting.content.chat.id)
            .also {
                runCatching { posting.content.id.toInt() }
                    .getOrNull()
                    ?.let { id -> it.replyToMessageId(id) }
            }
            .build()
        tgSender.execute(video)
        kotlin.runCatching {
            file.delete()
        }.getOrElse {
            file.deleteOnExit()
        }
    }

    private suspend fun sendText(posting: Posting, text: String) {
        tgSender.sendInternal(
            chatId = posting.content.chat.id,
            text = text,
            shouldTypeBeforeSend = false
        )
    }

    private suspend fun AbsSender.sendInternal(
        chatId: String,
        messageId: Int? = null,
        text: String,
        replyMessageId: Int? = null,
        enableHtml: Boolean = false,
        replyToUpdate: Boolean = false,
        customization: SendMessage.() -> Unit = { },
        shouldTypeBeforeSend: Boolean = false
    ) {

        if (shouldTypeBeforeSend) {
            val typeDelay = generateTypeDelay(text.length)
            this.execute(SendChatAction(chatId, "typing", null))
            delay(typeDelay)
        }

        val method = SendMessage(chatId, text).apply {
            enableHtml(enableHtml)
            if (replyMessageId != null) replyToMessageId = replyMessageId
            if (replyToUpdate) replyToMessageId = messageId
            customization()
        }

        tgSender.execute(method)
    }

    private fun generateTypeDelay(messageLength: Int): Long {
        val baseline = ln(if (messageLength >= 500) 500.0 else messageLength.toDouble())
        val min = baseline * ONE_SYMBOL_TYPING_SPEED_MILLS * 0.9f
        val max = baseline * ONE_SYMBOL_TYPING_SPEED_MILLS * 1.2f
        return randomInt(min.toInt(), max.toInt()).toLong()
    }

    private companion object {
        const val ONE_SYMBOL_TYPING_SPEED_MILLS = 60
    }

    private fun randomInt(from: Int, to: Int) = ThreadLocalRandom.current().nextInt(from, to)
}