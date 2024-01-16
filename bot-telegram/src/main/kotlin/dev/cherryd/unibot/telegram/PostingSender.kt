package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Posting
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.concurrent.ThreadLocalRandom


class PostingSender(
    private val tgSender: AbsSender
) {

    private val logger = KotlinLogging.logger { }

    suspend fun send(posting: Posting) {
        logger.info { "Sending posting: $posting" }

        when (val extra = posting.extra) {
            is Posting.Content.Extra.Text -> sendText(posting, extra.text)
            else -> {}
        }
    }

    private suspend fun sendText(posting: Posting, text: String) {
        tgSender.sendInternal(
            chatId = posting.content.chat.id,
            text = text,
            shouldTypeBeforeSend = true
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
        shouldTypeBeforeSend: Boolean = false,
        typeDelay: Pair<Int, Int> = 1000 to 2000
    ) {

        if (shouldTypeBeforeSend) {
            this.execute(SendChatAction(chatId, "typing", null))
            delay(randomInt(typeDelay.first, typeDelay.second).toLong())
        }

        val method = SendMessage(chatId, text).apply {
            enableHtml(enableHtml)
            if (replyMessageId != null) replyToMessageId = replyMessageId
            if (replyToUpdate) replyToMessageId = messageId
            customization()
        }

        tgSender.execute(method)
    }

    private fun randomInt(from: Int, to: Int) = ThreadLocalRandom.current().nextInt(from, to)
}