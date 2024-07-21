package dev.cherryd.unibot.interceptors

import dev.cherryd.unibot.core.ChatRepository
import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.UsersRepository
import dev.cherryd.unibot.core.interceptor.BotInteractor
import dev.cherryd.unibot.core.interceptor.PostInterceptor
import dev.cherryd.unibot.timeOf
import io.micrometer.core.instrument.MeterRegistry

class ChatStoreInterceptor(
    private val meter: MeterRegistry,
    private val chatsRepository: ChatRepository,
    private val usersRepository: UsersRepository
) : PostInterceptor {

    override suspend fun intercept(post: Post, botInteractor: BotInteractor) {
        meter.timeOf("unibot.store.save.chat_data") {
            chatsRepository.createChat(post.chat)
            usersRepository.saveUser(post.sender)
            usersRepository.linkUserToChat(post.sender, post.chat)
        }
    }
}