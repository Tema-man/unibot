package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry

class Router(
    private val responders: List<Responder>,
    private val meter: MeterRegistry
) {

    private val log = KotlinLogging.logger {}

    fun pickResponder(posting: Posting): Responder? = meter.timeOf("unibot.pickResponder") {
        log.info { "Picking responder for posting: $posting" }

        val enabledResponders = responders
            .filter { (it.getPriority(posting.settings) != Responder.Priority.DISABLED) }

        val responders = enabledResponders.filter { it.canHandle(posting) }
            .sortedByDescending { it.getPriority(posting.settings) }

        log.info { "There are ${enabledResponders.size} enabled responders, ${responders.size} can handle the posting" }
        val responder = responders.firstOrNull()
        log.info { if (responder != null) "Picked responder: $responder" else "No responder found. Skipping." }

        responder
    }
}