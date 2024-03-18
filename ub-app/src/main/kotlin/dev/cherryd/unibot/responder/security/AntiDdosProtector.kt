package dev.cherryd.unibot.responder.security

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Responder.Priority
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.Phrase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class AntiDdosProtector(
    private val dictionary: Dictionary,
    private val userCommandHistory: UserCommandHistory
) : Responder {

    override fun getPriority(settings: Settings) = Priority.HIGH

    override fun canHandle(posting: Posting): Boolean =
        posting.extra is Posting.Content.Extra.Command && posting.isUserBlocked

    override fun responseStream(incoming: Posting): Flow<Posting> {
        if (incoming.extra !is Posting.Content.Extra.Command) return emptyFlow()
        if (!incoming.isUserBlocked) return emptyFlow()
        return flowOf(dictionary.phraseAnswer(Phrase.STOP_DDOS, incoming))
    }

    private val Posting.isUserBlocked: Boolean
        get() = userCommandHistory.checkUserCommandLimit(content.sender.id)
}