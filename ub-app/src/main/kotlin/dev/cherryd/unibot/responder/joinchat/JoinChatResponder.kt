package dev.cherryd.unibot.responder.joinchat

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.Phrase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JoinChatResponder(
    private val dictionary: Dictionary
) : Responder {
    override fun getPriority(settings: Settings) = Responder.Priority.HIGH
    override fun canHandle(post: Post): Boolean = post.extra is Post.Extra.ChatEvent.BotAdded
    override fun responseStream(post: Post): Flow<Post> = flow {
        emit(dictionary.phraseAnswer(Phrase.USER_ENTER_CHAT, post))
    }
}