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
    override fun responseStream(post: Post): Flow<Post> = flow {
        if (!canHandle(post)) return@flow
        handleCommand(this, post)
    }

    abstract suspend fun handleCommand(flow: FlowCollector<Post>, post: Post)

}