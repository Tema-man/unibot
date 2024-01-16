package dev.cherryd.unibot.processors.security

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.PostingTransformer

class AntiDdosProtector : PostingTransformer {

    override suspend fun transform(incoming: Posting): Posting {
        return incoming.copy(content = incoming.content.copy(text = "Hello"))
    }

}