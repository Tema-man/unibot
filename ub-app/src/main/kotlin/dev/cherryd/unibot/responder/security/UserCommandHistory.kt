package dev.cherryd.unibot.responder.security

import java.util.*

class UserCommandHistory {

    private val commandHistory = mutableMapOf<String, Stack<Long>>()

    fun checkUserCommandLimit(userId: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastMinute = currentTime - 60_000
        val lastFiveMinutes = currentTime - 300_000

        val commands = commandHistory.getOrPut(userId) { Stack() }
        if (!commands.empty() && commands.peek() < lastFiveMinutes) {
            commands.clear()
        }
        commands.add(currentTime)

        return commands.count { it > lastMinute } > 2
    }
}