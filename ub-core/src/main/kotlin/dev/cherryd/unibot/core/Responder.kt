package dev.cherryd.unibot.core

import kotlinx.coroutines.flow.Flow

interface Responder {

    fun getPriority(settings: Settings): Priority
    fun canHandle(post: Post): Boolean
    fun responseStream(incoming: Post): Flow<Post>

    enum class Priority {
        DISABLED, LOW, MEDIUM, HIGH
    }
}