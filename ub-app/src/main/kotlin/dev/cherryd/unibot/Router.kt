package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import io.micrometer.core.instrument.MeterRegistry

class Router(
    private val responders: List<Responder>,
    private val meter: MeterRegistry
) {

    fun pickResponder(posting: Posting): Responder = meter.timeOf("unibot.pickResponder") {
        val enabledTransformers = responders
            .filter { (it.getPriority(posting.settings) != Responder.Priority.DISABLED) }

        val transformers = enabledTransformers.filter { it.canHandle(posting) }
            .sortedByDescending { it.getPriority(posting.settings) }

        transformers.firstOrNull() ?: enabledTransformers.random()
    }
}