package dev.cherryd.unibot.di

import dev.cherryd.unibot.Router
import dev.cherryd.unibot.core.PostingTransformer
import dev.cherryd.unibot.processors.security.AntiDdosProtector

object RouterModule {

    private val transformers = listOf<PostingTransformer>(
        AntiDdosProtector()
    )

    fun provideRouter() = Router(
        transformers
    )
}