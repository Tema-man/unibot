package dev.cherryd.unibot.responder.security

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Responder.Priority
import dev.cherryd.unibot.core.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AntiDdosProtector : Responder {

    override fun getPriority(settings: Settings) = Priority.LOW
    override fun canHandle(posting: Posting): Boolean  = true

    override fun responseStream(incoming: Posting): Flow<Posting> = flowOf(
        incoming.answer(Posting.Content.Extra.Text("Hello from AntiDdosProtector"))
    )
}