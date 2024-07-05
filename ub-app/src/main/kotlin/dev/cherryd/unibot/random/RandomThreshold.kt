package dev.cherryd.unibot.random

import java.util.concurrent.ThreadLocalRandom

class RandomThreshold(
    private val startProbability: Float = 0.2f,
    private val increaseSpeed: Float = 0.01f
) {

    @Volatile
    private var nextProbability: Float = startProbability

    @Synchronized
    fun checkRandom(): Boolean {
        val random = ThreadLocalRandom.current()
        nextProbability += random.nextFloat(0f, increaseSpeed)
        if (nextProbability > 0.9f) nextProbability = startProbability

        return random.nextFloat(0f, 1f) <= nextProbability
    }
}