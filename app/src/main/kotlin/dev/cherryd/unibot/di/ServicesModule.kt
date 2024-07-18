package dev.cherryd.unibot.di

import dev.cherryd.unibot.ChatService

object ServicesModule {

    val chatService: ChatService by lazy {
        ChatService(
            meter = MicrometerModule.meterRegistry,
            chatsRepository = RepositoriesModule.chatsRepository,
            usersRepository = RepositoriesModule.usersRepository,
            messagesRepository = RepositoriesModule.messagesRepository,
        )
    }
}