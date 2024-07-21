package dev.cherryd.unibot.core.interceptor

import dev.cherryd.unibot.core.Post

interface PostInterceptor {

    suspend fun intercept(post: Post, botInteractor: BotInteractor)
}