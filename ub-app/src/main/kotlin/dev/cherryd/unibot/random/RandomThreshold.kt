package dev.cherryd.unibot.random

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.ChatRepository
import java.util.concurrent.ThreadLocalRandom

class RandomThreshold(
    private val increaseSpeed: Float = 0.01f,
    private val chatRepository: ChatRepository
) {
    private val random = ThreadLocalRandom.current()

    fun isHit(chat: Chat): Boolean {
        var probability = chat.settings.randomThresholdProbability + random.nextFloat(0f, increaseSpeed)
        val hit = random.nextFloat(0f, 1f) <= probability
        if (hit || probability >= 1f) probability = 0f
        val newSettings = chat.settings.copy(randomThresholdProbability = probability)
        val newChat = chat.copy(settings = newSettings)
        chatRepository.updateChat(newChat)
        return hit
    }
}