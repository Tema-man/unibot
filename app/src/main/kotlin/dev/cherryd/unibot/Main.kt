package dev.cherryd.unibot

import dev.cherryd.unibot.di.RelaysModule
import dev.cherryd.unibot.di.RouterModule
import io.github.oshai.kotlinlogging.KotlinLogging


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger { }
    logger.info { "Starting UniBot Application" }

    val unibot = Unibot(
        relays = RelaysModule.provideRelays(),
        router = RouterModule.provideRouter()
    )

    unibot.start()

    while (true) {
        val input = readln()
        if (input.equals("q", ignoreCase = true)) {
            logger.info { "Quiting the app" }
            unibot.stop()
            break
        }
    }
}

