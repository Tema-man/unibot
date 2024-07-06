package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.data.MessagesRepository
import dev.cherryd.unibot.random.RandomThreshold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RandomMessageResponder(
    private val messagesRepository: MessagesRepository
) : Responder {

    private val randomThreshold = RandomThreshold(increaseSpeed = 0.01f)

    override fun getPriority(settings: Settings) = Responder.Priority.LOW

    override fun canHandle(post: Post): Boolean = randomThreshold.isHit()

    override fun responseStream(incoming: Post): Flow<Post> = flow {
        val message = messagesRepository.getRandomPosting()
        if (message.isNotBlank()) emit(incoming.textAnswer(true) { message })
    }
}