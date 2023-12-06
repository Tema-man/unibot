package dev.cherryd.unibot

import io.github.oshai.kotlinlogging.KotlinLogging

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger { }
    logger.info { "Starting UniBot Application" }

    Component.unibot.apply {
        registerRelay(Component.TelegramRelay())
        start()
    }

    while (true) {
        val input = readln()
        if (input.equals("q", ignoreCase = true)) {
            logger.info { "Quiting the app" }
            Component.unibot.stop()
            break
        }
    }
}

