package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow

interface Relay {

    /**
     * Basically a stream of messages from a particular bot.
     */
    fun incomingPostingsFlow(): Flow<Posting>
    suspend fun post(posting: Posting)

    suspend fun start()
    suspend fun afterStartSetup()
    suspend fun stop()
    suspend fun restart()
}