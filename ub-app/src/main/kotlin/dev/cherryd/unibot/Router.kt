package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.responder.security.AntiDdosProtector
import io.github.oshai.kotlinlogging.KotlinLogging

class Router(
    private val responders: List<Responder>
) {

    private val logger = KotlinLogging.logger("Router")

    fun pickResponder(posting: Posting): Responder {
        val availableTransformers = responders
            .filter {
                (it.getPriority(posting.settings) != Responder.Priority.DISABLED) && it.canHandle(posting)
            }
            .ifEmpty { listOf(AntiDdosProtector()) }
            .sortedByDescending { it.getPriority(posting.settings) }

        val processor = availableTransformers.firstOrNull() ?: availableTransformers.random()

        logger.info { "Selected transformer: ${processor.javaClass.name}" }

        return processor
    }
}