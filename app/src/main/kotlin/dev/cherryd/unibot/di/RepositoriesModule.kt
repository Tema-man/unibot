package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.CommandResponder
import dev.cherryd.unibot.core.CommandsRepository
import dev.cherryd.unibot.responder.help.CommandsRepositoryImpl
import dev.cherryd.unibot.responder.quote.QuoteRepository

object RepositoriesModule {

    val quoteRepository by lazy { QuoteRepository() }

    val commandsRepository: CommandsRepository by lazy { CommandsRepositoryImpl(RouterModule.responders.filterIsInstance<CommandResponder>()) }

}