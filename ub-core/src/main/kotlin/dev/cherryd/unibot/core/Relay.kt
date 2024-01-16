package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow

interface Relay {

    /**
     * Basically a stream of messages from a particular bot.
     */
    fun incomingPostingsFlow(): Flow<Posting>
    suspend fun post(posting: Posting)
    fun start()
    fun stop()
    fun restart()
}