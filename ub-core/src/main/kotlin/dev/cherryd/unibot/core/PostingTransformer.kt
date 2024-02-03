package dev.cherryd.unibot.core

interface PostingTransformer {

    fun getPriority(settings: Settings): Priority
    suspend fun transform(incoming: Posting): Posting?

    enum class Priority {
        DISABLED, LOW, MEDIUM, HIGH
    }
}