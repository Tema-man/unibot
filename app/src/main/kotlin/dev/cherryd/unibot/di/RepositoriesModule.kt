package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.ChatRepository
import dev.cherryd.unibot.core.CommandResponder
import dev.cherryd.unibot.core.CommandsRepository
import dev.cherryd.unibot.core.UsersRepository
import dev.cherryd.unibot.data.ChatRepositoryImpl
import dev.cherryd.unibot.data.MessagesRepository
import dev.cherryd.unibot.data.PidorsRepository
import dev.cherryd.unibot.data.UsersRepositoryImpl
import dev.cherryd.unibot.responder.help.CommandsRepositoryImpl
import dev.cherryd.unibot.responder.quote.QuoteRepository

object RepositoriesModule {
    val quoteRepository by lazy { QuoteRepository() }
    val commandsRepository: CommandsRepository by lazy { CommandsRepositoryImpl(RouterModule.responders.filterIsInstance<CommandResponder>()) }
    val chatRepository: ChatRepository by lazy { ChatRepositoryImpl(AppModule.database) }
    val messagesRepository: MessagesRepository by lazy { MessagesRepository(AppModule.database) }
    val usersRepository: UsersRepository by lazy { UsersRepositoryImpl(AppModule.database) }
    val pidorsRepository: PidorsRepository by lazy { PidorsRepository(AppModule.database) }
}