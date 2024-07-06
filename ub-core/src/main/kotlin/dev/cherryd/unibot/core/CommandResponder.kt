package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

abstract class CommandResponder : Responder {

    abstract val commandDescription: CommandDescription

    override fun canHandle(post: Post): Boolean {
        val extra = post.extra
        if (extra !is Post.Extra.Command) return false
        return extra.command == commandDescription.command
    }
    override fun responseStream(incoming: Post): Flow<Post> = flow {
        if (!canHandle(incoming)) return@flow
        handleCommand(this, incoming)
    }

    abstract suspend fun handleCommand(flow: FlowCollector<Post>, incoming: Post)

}