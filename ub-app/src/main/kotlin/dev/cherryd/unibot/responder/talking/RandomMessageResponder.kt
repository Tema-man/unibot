package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.data.MessagesRepository
import dev.cherryd.unibot.random.RandomThreshold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RandomMessageResponder(
    private val messagesRepository: MessagesRepository,
    private val randomThreshold: RandomThreshold
) : Responder {

    override fun getPriority(settings: Settings) = Responder.Priority.LOW

    override fun canHandle(post: Post): Boolean = true

    override fun responseStream(post: Post): Flow<Post> = flow {
        if (!randomThreshold.isHit(post.chat)) return@flow
        val message = messagesRepository.getRandomPosting()
        if (message.isNotBlank()) emit(post.textAnswer(true) { message })
    }
}