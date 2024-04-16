package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.responder.quote.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BotMentionResponder(
    private val quoteRepository: QuoteRepository
) : Responder {

    override fun getPriority(settings: Settings) = Responder.Priority.LOW

    override fun canHandle(posting: Posting): Boolean =
        isBotMention(posting) || isBotAliasMention(posting) || isReplyToBot(posting)

    override fun responseStream(incoming: Posting): Flow<Posting> = flow {
        if (!canHandle(incoming)) return@flow
        val answer = quoteRepository.getRandom()
        emit(incoming.textAnswer { answer })
    }

    private fun isBotMention(posting: Posting): Boolean {
        val botName = posting.settings.bot.name
        return posting.extra.text.contains("@$botName", ignoreCase = true)
    }

    private fun isBotAliasMention(posting: Posting): Boolean {
        val botAliases = posting.settings.bot.aliases
        return botAliases.any { posting.extra.text.contains("@$it", ignoreCase = true) }
    }

    private fun isReplyToBot(posting: Posting): Boolean =
        posting.content.reply?.sender?.name == posting.settings.bot.name
}