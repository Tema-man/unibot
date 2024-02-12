package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

abstract class CommandResponder : Responder {

    abstract val commandDescription: CommandDescription

    override fun canHandle(posting: Posting): Boolean {
        val extra = posting.extra
        if (extra !is Posting.Content.Extra.Command) return false
        return extra.command == commandDescription.command
    }
    override fun responseStream(incoming: Posting): Flow<Posting> = flow {
        if (!canHandle(incoming)) return@flow
        handleCommand(this, incoming)
    }

    abstract suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting)

}