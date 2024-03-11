package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.responder.security.AntiDdosProtector

class Router(
    private val responders: List<Responder>,
    private val antiDdosProtector: AntiDdosProtector
) {
    fun pickResponder(posting: Posting): Responder {
        val availableTransformers = responders
            .filter { (it.getPriority(posting.settings) != Responder.Priority.DISABLED) && it.canHandle(posting) }
            .ifEmpty { listOf(antiDdosProtector) }
            .sortedByDescending { it.getPriority(posting.settings) }

        val processor = availableTransformers.firstOrNull() ?: availableTransformers.random()
        return processor
    }
}