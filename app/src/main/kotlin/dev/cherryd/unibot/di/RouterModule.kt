package dev.cherryd.unibot.di

import dev.cherryd.unibot.Router
import dev.cherryd.unibot.responder.help.HelpCommandResponder
import dev.cherryd.unibot.responder.joinchat.JoinChatResponder
import dev.cherryd.unibot.responder.quote.QuoteResponder
import dev.cherryd.unibot.responder.quote.TopHistoryResponder
import dev.cherryd.unibot.responder.security.AntiDdosProtector
import dev.cherryd.unibot.responder.tiktok.TikTokVideoDownloader

object RouterModule {

    val responders = listOf(
        AntiDdosProtector(),
        TikTokVideoDownloader(AppModule.ytDlpWrapper),
        QuoteResponder(RepositoriesModule.quoteRepository),
        JoinChatResponder(),
        TopHistoryResponder(RepositoriesModule.quoteRepository)
    )

    fun provideRouter() = Router(
        responders + HelpCommandResponder(RepositoriesModule.commandsRepository),
    )
}