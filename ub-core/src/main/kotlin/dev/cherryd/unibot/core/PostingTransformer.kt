package dev.cherryd.unibot.core

interface PostingTransformer {
    suspend fun transform(incoming: Posting): Posting
}