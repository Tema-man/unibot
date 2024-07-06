package dev.cherryd.unibot.responder.security

import dev.cherryd.unibot.core.Post
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

    override fun canHandle(post: Post): Boolean =
        post.extra is Post.Extra.Command && post.isUserBlocked

    override fun responseStream(incoming: Post): Flow<Post> {
        if (!canHandle(incoming)) return emptyFlow()
        return flowOf(dictionary.phraseAnswer(Phrase.STOP_DDOS, incoming))
    }

    private val Post.isUserBlocked: Boolean
        get() = userCommandHistory.checkUserCommandLimit(sender.id)
}