package dev.cherryd.unibot

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.UsersRepository
import dev.cherryd.unibot.data.ChatsRepository
import dev.cherryd.unibot.data.MessagesRepository
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChatService(
    private val meter: MeterRegistry,
    private val chatsRepository: ChatsRepository,
    private val messagesRepository: MessagesRepository,
    private val usersRepository: UsersRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun storePost(post: Post) {
        coroutineScope.launch {
            meter.timeOf("unibot.store.save.posting") { messagesRepository.savePosting(post) }
            storeChatData(post)
        }
    }

    private fun storeChatData(post: Post) {
        meter.timeOf("unibot.store.save.chat_data") {
            usersRepository.saveUser(post.sender)
            chatsRepository.saveChat(post.chat)
            usersRepository.linkUserToChat(post.sender, post.chat)
        }
    }
}
