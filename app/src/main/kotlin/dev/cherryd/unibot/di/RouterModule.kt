package dev.cherryd.unibot.di

import dev.cherryd.unibot.Router
import dev.cherryd.unibot.random.RandomThreshold
import dev.cherryd.unibot.responder.help.HelpCommandResponder
import dev.cherryd.unibot.responder.joinchat.JoinChatResponder
import dev.cherryd.unibot.responder.pidor.RandomPidorResponder
import dev.cherryd.unibot.responder.quote.QuoteResponder
import dev.cherryd.unibot.responder.quote.TopHistoryResponder
import dev.cherryd.unibot.responder.security.AntiDdosProtector
import dev.cherryd.unibot.responder.security.UserCommandHistory
import dev.cherryd.unibot.responder.talking.BotMentionResponder
import dev.cherryd.unibot.responder.talking.HuificatorResponder
import dev.cherryd.unibot.responder.talking.RandomMessageResponder
import dev.cherryd.unibot.responder.tiktok.TikTokVideoDownloader

object RouterModule {

    private val randomThreshold = RandomThreshold(chatRepository = RepositoriesModule.chatRepository)
    private val userCommandHistory = UserCommandHistory()
    private val antiDdosProtector = AntiDdosProtector(
        dictionary = AppModule.dictionary,
        userCommandHistory = userCommandHistory
    )

    val responders = listOf(
        antiDdosProtector,
        TikTokVideoDownloader(AppModule.ytDlpWrapper),
        QuoteResponder(RepositoriesModule.quoteRepository),
        JoinChatResponder(AppModule.dictionary),
        TopHistoryResponder(RepositoriesModule.quoteRepository),
        HuificatorResponder(
            randomThreshold = randomThreshold
        ),
        BotMentionResponder(
            quoteRepository = RepositoriesModule.quoteRepository,
            messagesRepository = RepositoriesModule.messagesRepository,
            randomThreshold = randomThreshold
        ),
        RandomMessageResponder(
            messagesRepository = RepositoriesModule.messagesRepository,
            randomThreshold = randomThreshold
        ),
        RandomPidorResponder(
            pidorsRepository = RepositoriesModule.pidorsRepository,
            usersRepository = RepositoriesModule.usersRepository,
            dictionary = AppModule.dictionary,
        ),
    )

    private val helpCommandResponder = HelpCommandResponder(
        commandsRepository = RepositoriesModule.commandsRepository,
        dictionary = AppModule.dictionary,
    )

    fun provideRouter() = Router(
        responders = responders + helpCommandResponder,
        meter = MicrometerModule.meterRegistry
    )
}