package dev.cherryd.unibot.core.random

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ln

class TypingDelayGenerator {

    fun generateTypingDelay(messageLength: Int): Long {
        val baseline = ln(messageLength.coerceAtMost(500).toDouble())
        val min = baseline * ONE_SYMBOL_TYPING_SPEED_MILLS * 0.9f
        val max = baseline * ONE_SYMBOL_TYPING_SPEED_MILLS * 1.2f
        return randomInt(min.toInt(), max.toInt()).toLong()
    }

    fun generateThinkingDelay(messagesCount: Int = 1): Long {
        val baseline = messagesCount.coerceAtMost(50).toDouble()
        val min = baseline * THINKING_SPEED_MILLS * 0.9f
        val max = baseline * THINKING_SPEED_MILLS * 1.2f
        return randomInt(min.toInt(), max.toInt()).toLong()
    }

    private companion object {
        const val ONE_SYMBOL_TYPING_SPEED_MILLS = 90
        const val THINKING_SPEED_MILLS = 150
    }

    private fun randomInt(from: Int, to: Int) = ThreadLocalRandom.current().nextInt(from, to)
}