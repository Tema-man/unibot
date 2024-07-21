package dev.cherryd.unibot.core

import dev.cherryd.unibot.core.interceptor.BotInteractor
import kotlinx.coroutines.flow.Flow

interface Relay {

    /**
     * Basically a stream of messages from a particular bot.
     */
    fun incomingPostingsFlow(): Flow<Post>
    val interactor: BotInteractor
    suspend fun post(post: Post)

    suspend fun start()
    suspend fun afterStartSetup()
    suspend fun stop()
    suspend fun restart()
}