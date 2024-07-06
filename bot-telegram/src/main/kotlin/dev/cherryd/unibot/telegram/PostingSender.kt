package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Post
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

    suspend fun send(post: Post) {
        when (val extra = post.extra) {
            is Post.Extra.Video -> sendVideo(post, extra.file)
            is Post.Extra.ChatEvent.SendingVideo -> {
                tgSender.execute(SendChatAction(post.chat.id, "upload_video", null))
            }

            else -> if (extra.text.isNotBlank()) sendText(post, extra.text)
        }
    }

    private fun sendVideo(post: Post, file: File) {
        val video = SendVideo
            .builder()
            .video(InputFile(file))
            .chatId(post.chat.id)
            .also {
                runCatching { post.id.toInt() }
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

    private suspend fun sendText(post: Post, text: String) {
        tgSender.sendInternal(
            chatId = post.chat.id,
            messageId = post.id.toIntOrNull(),
            text = text,
            shouldTypeBeforeSend = text.length > 10,
            replyToUpdate = post.reply != null
        )
    }

    private suspend fun AbsSender.sendInternal(
        chatId: String,
        messageId: Int? = null,
        text: String,
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