package dev.cherryd.unibot.di

import dev.cherryd.unibot.interceptors.ChatStoreInterceptor

object InterceptorsModule {

    private val chatStoreInterceptor: ChatStoreInterceptor by lazy {
        ChatStoreInterceptor(
            meter = MicrometerModule.meterRegistry,
            chatsRepository = RepositoriesModule.chatRepository,
            messagesRepository = RepositoriesModule.messagesRepository,
            usersRepository = RepositoriesModule.usersRepository
        )
    }

    val interceptors = listOf(
        chatStoreInterceptor
    )
}