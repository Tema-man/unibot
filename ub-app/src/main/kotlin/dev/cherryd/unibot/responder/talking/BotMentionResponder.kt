package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.data.MessagesRepository
import dev.cherryd.unibot.random.RandomThreshold
import dev.cherryd.unibot.responder.quote.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BotMentionResponder(
    private val quoteRepository: QuoteRepository,
    private val messagesRepository: MessagesRepository
) : Responder {

    private val randomThreshold = RandomThreshold(increaseSpeed = 0.2f)

    override fun getPriority(settings: Settings) = Responder.Priority.LOW

    override fun canHandle(post: Post): Boolean =
        isBotMention(post) || isBotAliasMention(post) || isReplyToBot(post)

    override fun responseStream(incoming: Post): Flow<Post> = flow {
        if (!canHandle(incoming)) return@flow
        val answer = if (randomThreshold.isHit()) {
            quoteRepository.getRandom()
        } else {
            messagesRepository.getRandomPosting()
        }
        emit(incoming.textAnswer(true) { answer })
    }

    private fun isBotMention(post: Post): Boolean {
        val botName = post.settings.bot.name
        return post.extra.text.contains(botName, ignoreCase = true)
    }

    private fun isBotAliasMention(post: Post): Boolean {
        val botAliases = post.settings.bot.aliases
        return botAliases.any { post.extra.text.contains(it, ignoreCase = true) }
    }

    private fun isReplyToBot(post: Post): Boolean = with(post) {
        reply?.sender?.id == settings.bot.id || reply?.sender?.name == settings.bot.name
    }
}