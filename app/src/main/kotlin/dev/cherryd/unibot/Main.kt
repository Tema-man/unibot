package dev.cherryd.unibot

import dev.cherryd.unibot.di.RelaysModule
import dev.cherryd.unibot.di.RouterModule
import io.github.oshai.kotlinlogging.KotlinLogging

fun main() {
    val logger = KotlinLogging.logger { }
    logger.info { "Starting UniBot Application" }

    val unibot = Unibot(
        relays = RelaysModule.provideRelays(),
        router = RouterModule.provideRouter()
    ).apply {
        start()
    }

    while (unibot.isRunning()) {
        Thread.sleep(10000)
    }
    unibot.stop()
}

