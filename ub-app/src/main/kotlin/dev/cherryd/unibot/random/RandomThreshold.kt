package dev.cherryd.unibot.random

import java.util.concurrent.ThreadLocalRandom

class RandomThreshold(
    private val startProbability: Float = 0.2f,
    private val increaseSpeed: Float = 0.01f
) {
    @Volatile
    private var nextProbability: Float = startProbability
    private val random = ThreadLocalRandom.current()

    @Synchronized
    fun isHit(): Boolean {
        nextProbability += random.nextFloat(0f, increaseSpeed)
        val hit = random.nextFloat(0f, 1f) <= nextProbability
        if (hit || nextProbability > 0.9f) reset()
        return hit
    }

    @Synchronized
    fun reset() {
        nextProbability = startProbability
    }
}