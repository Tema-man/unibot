package dev.cherryd.unibot.di

import dev.cherryd.unibot.Router
import dev.cherryd.unibot.processors.security.AntiDdosProtector
import dev.cherryd.unibot.processors.tiktok.TikTokVideoDownloader

object RouterModule {

    private val transformers = listOf(
        AntiDdosProtector(),
        TikTokVideoDownloader(AppModule.environment)
    )

    fun provideRouter() = Router(
        transformers
    )
}