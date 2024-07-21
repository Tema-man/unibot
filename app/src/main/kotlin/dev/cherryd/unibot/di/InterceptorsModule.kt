package dev.cherryd.unibot.di

import dev.cherryd.unibot.interceptors.ChatLogInterceptor
import dev.cherryd.unibot.interceptors.ChatStoreInterceptor

object InterceptorsModule {

    private val chatStoreInterceptor: ChatStoreInterceptor by lazy {
        ChatStoreInterceptor(
            meter = MicrometerModule.meterRegistry,
            chatsRepository = RepositoriesModule.chatRepository,
            usersRepository = RepositoriesModule.usersRepository
        )
    }

    private val chatLogInterceptor: ChatLogInterceptor by lazy {
        ChatLogInterceptor(
            meter = MicrometerModule.meterRegistry,
            messagesRepository = RepositoriesModule.messagesRepository
        )
    }

    val interceptors = listOf(
        chatStoreInterceptor,
        chatLogInterceptor
    )
}