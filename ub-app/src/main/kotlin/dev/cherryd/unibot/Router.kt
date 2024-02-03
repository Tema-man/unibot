package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.PostingTransformer
import dev.cherryd.unibot.processors.security.AntiDdosProtector
import io.github.oshai.kotlinlogging.KotlinLogging

class Router(
    private val transformers: List<PostingTransformer>
) {

    private val logger = KotlinLogging.logger { }
    fun pickPostingTransformer(posting: Posting): PostingTransformer {
        val availableTransformers = transformers
            .filter { it.getPriority(posting.settings) != PostingTransformer.Priority.DISABLED }
            .ifEmpty { listOf(AntiDdosProtector()) }
            .sortedByDescending { it.getPriority(posting.settings) }

        val processor = availableTransformers.firstOrNull() ?: availableTransformers.random()

        logger.info { "Selected transformer: ${processor.javaClass.name}" }

        return processor
    }
}