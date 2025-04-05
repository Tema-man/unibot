package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.random.TypingDelayGenerator
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
    private val tgSender: AbsSender,
    private val delayGenerator: TypingDelayGenerator
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
            shouldTypeBeforeSend = text.length > 5,
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
            val typeDelay = delayGenerator.generateTypingDelay(text.length)
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
}