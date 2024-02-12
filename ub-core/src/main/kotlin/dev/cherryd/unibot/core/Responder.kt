package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow

interface Responder {

    fun getPriority(settings: Settings): Priority

    fun canHandle(posting: Posting): Boolean
    fun responseStream(incoming: Posting): Flow<Posting>

    enum class Priority {
        DISABLED, LOW, MEDIUM, HIGH
    }
}