package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.CommandsRepository
import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Relay
import kotlinx.coroutines.flow.Flow
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault

class TelegramRelay(
    environment: Environment,
    private val commmandsRepository: CommandsRepository
) : Relay {

    private val tgBot = TelegramBot(environment)
    private val postingSender = PostingSender(tgBot)

    override fun incomingPostingsFlow(): Flow<Post> = tgBot.observePostings()

    override suspend fun post(post: Post) {
        postingSender.send(post)
    }

    override suspend fun afterStartSetup() {
        tgBot.execute(DeleteMyCommands.builder().scope(BotCommandScopeAllGroupChats()).build())
        tgBot.execute(DeleteMyCommands.builder().scope(BotCommandScopeAllPrivateChats()).build())
        tgBot.execute(DeleteMyCommands.builder().scope(BotCommandScopeDefault()).build())

        val botCommands = commmandsRepository.getCommands().map { BotCommand(it.command, it.description) }
        tgBot.execute(
            SetMyCommands.builder()
                .commands(botCommands)
                .scope(BotCommandScopeDefault())
                .build()
        )
    }

    override suspend fun start() {
        tgBot.start()
    }

    override suspend fun stop() {
        tgBot.stop()
    }

    override suspend fun restart() {
        tgBot.stop()
        tgBot.start()
    }
}