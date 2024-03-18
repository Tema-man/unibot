package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder

class Router(private val responders: List<Responder>) {

    fun pickResponder(posting: Posting): Responder {
        val enabledTransformers = responders
            .filter { (it.getPriority(posting.settings) != Responder.Priority.DISABLED) }

        val transformers = enabledTransformers.filter { it.canHandle(posting) }
            .sortedByDescending { it.getPriority(posting.settings) }

        val processor = transformers.firstOrNull() ?: enabledTransformers.random()
        return processor
    }
}