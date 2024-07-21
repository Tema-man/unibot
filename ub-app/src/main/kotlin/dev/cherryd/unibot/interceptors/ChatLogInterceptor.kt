package dev.cherryd.unibot.interceptors

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.interceptor.BotInteractor
import dev.cherryd.unibot.core.interceptor.PostInterceptor
import dev.cherryd.unibot.data.MessagesRepository
import dev.cherryd.unibot.timeOf
import io.micrometer.core.instrument.MeterRegistry

class ChatLogInterceptor(
    private val meter: MeterRegistry,
    private val messagesRepository: MessagesRepository
) : PostInterceptor {

    private val chatLogRegex = Regex("[а-яА-Яё0-9\\s,.!?\\-]+")

    override suspend fun intercept(post: Post, botInteractor: BotInteractor) {
        meter.timeOf("unibot.store.save.posting") {
            if (post.extra !is Post.Extra.Text) return@timeOf

            post.extra.text
                .takeIf { post.settings.aliases.none { alias -> it.contains(alias, ignoreCase = true) } }
                ?.takeIf { it.split(" ").size in (1..10) }
                ?.takeIf { it.length < 600 }
                ?.takeIf { chatLogRegex.matches(it) } ?: return@timeOf

            messagesRepository.savePosting(post)
        }
    }
}