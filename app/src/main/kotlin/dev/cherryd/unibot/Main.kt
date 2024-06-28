package dev.cherryd.unibot

import dev.cherryd.unibot.di.MicrometerModule
import dev.cherryd.unibot.di.RelaysModule
import dev.cherryd.unibot.di.RepositoriesModule
import dev.cherryd.unibot.di.RouterModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics

fun main() {
    val logger = KotlinLogging.logger { }
    logger.info { "Starting UniBot Application" }

    val unibot = Unibot(
        relays = RelaysModule.provideRelays(),
        router = RouterModule.provideRouter(),
        meter = MicrometerModule.meterRegistry,
        chatsRepository = RepositoriesModule.chatsRepository
    ).apply {
        start()
    }

    embeddedServer(CIO, module = { module() }).start(wait = true)

    unibot.stop()
}

private fun Application.module() {
    install(ShutDownUrl.ApplicationCallPlugin)
    install(MicrometerMetrics) {
        registry = MicrometerModule.meterRegistry
        meterBinders = listOf(
            JvmMemoryMetrics(), JvmGcMetrics(), ProcessorMetrics()
        )
    }
    routing {
        get("/metrics") {
            call.respond(MicrometerModule.prometheusMeterRegistry.scrape())
        }
    }
}
