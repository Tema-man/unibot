package dev.cherryd.unibot.di

import dev.cherryd.unibot.Router
import dev.cherryd.unibot.responder.help.HelpCommandResponder
import dev.cherryd.unibot.responder.joinchat.JoinChatResponder
import dev.cherryd.unibot.responder.quote.QuoteResponder
import dev.cherryd.unibot.responder.quote.TopHistoryResponder
import dev.cherryd.unibot.responder.security.AntiDdosProtector
import dev.cherryd.unibot.responder.talking.HuificatorResponder
import dev.cherryd.unibot.responder.tiktok.TikTokVideoDownloader

object RouterModule {

    val antiDdosProtector = AntiDdosProtector(AppModule.dictionary)

    val responders = listOf(
        antiDdosProtector,
        TikTokVideoDownloader(AppModule.ytDlpWrapper),
        QuoteResponder(RepositoriesModule.quoteRepository),
        JoinChatResponder(),
        TopHistoryResponder(RepositoriesModule.quoteRepository),
        HuificatorResponder()
    )

    private val helpCommandResponder = HelpCommandResponder(
        commandsRepository = RepositoriesModule.commandsRepository,
        dictionary = AppModule.dictionary,
    )

    fun provideRouter() = Router(
        responders = responders + helpCommandResponder,
        antiDdosProtector = antiDdosProtector
    )
}