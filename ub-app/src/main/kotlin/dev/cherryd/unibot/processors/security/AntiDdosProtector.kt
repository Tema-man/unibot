package dev.cherryd.unibot.processors.security

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.PostingTransformer
import dev.cherryd.unibot.core.PostingTransformer.Priority
import dev.cherryd.unibot.core.Settings

class AntiDdosProtector : PostingTransformer {

    override fun getPriority(settings: Settings) = Priority.LOW

    override suspend fun transform(incoming: Posting): Posting {
        return incoming.answer(Posting.Content.Extra.Text("Hello from AntiDdosProtector"))
    }

}