package dev.cherryd.unibot.random

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.SettingsRepository
import java.util.concurrent.ThreadLocalRandom

class RandomThreshold(
    private val increaseSpeed: Float = 0.01f,
    private val settingsRepository: SettingsRepository
) {
    private val random = ThreadLocalRandom.current()

    @Synchronized
    fun isHit(chat: Chat, settings: Settings.Chat): Boolean {
        var probability = settings.randomThresholdProbability + random.nextFloat(0f, increaseSpeed)
        val hit = random.nextFloat(0f, 1f) <= probability
        if (hit || probability >= 1f) probability = 0f
        val newSettings = settings.copy(randomThresholdProbability = probability)
        settingsRepository.saveChatSettings(chat, newSettings)
        return hit
    }
}