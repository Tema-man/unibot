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
        logger.info { "Searching processor for a posting: $posting" }

        return AntiDdosProtector()// if there is no intent processor ddos protector by default ?
    }
}