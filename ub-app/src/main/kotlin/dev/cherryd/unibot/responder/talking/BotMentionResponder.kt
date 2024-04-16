package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class BotMentionResponder : Responder {

    override fun getPriority(settings: Settings) = Responder.Priority.LOW

    override fun canHandle(posting: Posting): Boolean {
        TODO("Not yet implemented")
    }

    override fun responseStream(incoming: Posting): Flow<Posting> {
        return emptyFlow()
    }
}