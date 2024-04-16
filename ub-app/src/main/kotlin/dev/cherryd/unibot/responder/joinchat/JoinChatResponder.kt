package dev.cherryd.unibot.responder.joinchat

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JoinChatResponder : Responder {
    override fun getPriority(settings: Settings) = Responder.Priority.HIGH
    override fun canHandle(posting: Posting): Boolean =
        posting.extra is Posting.Content.Extra.ChatEvent.BotAdded

    override fun responseStream(incoming: Posting): Flow<Posting> = flow {
        if (!canHandle(incoming)) return@flow
    }
}