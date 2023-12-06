package dev.cherryd.unibot

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.PostingTransformer
import dev.cherryd.unibot.core.Router
import dev.cherryd.unibot.core.Unibot
import dev.cherryd.unibot.core.processors.security.AntiDdosProtector
import dev.cherryd.unibot.telegram.TelegramRelay

object Component {

    val environment = Environment()
    val settingsRepository = SettingsRepositoryImpl()

    val transformers = listOf<PostingTransformer>(
        AntiDdosProtector()
    )

    val router = Router(
        transformers
    )

    val unibot = Unibot(
        router = router
    )

    private var telegramRelay: TelegramRelay? = null
    fun TelegramRelay(): TelegramRelay {
        if (telegramRelay != null) throw IllegalStateException("Telegram relay was already created")
        telegramRelay = TelegramRelay(
            environment,
            settingsRepository
        )
        return telegramRelay!!
    }
}