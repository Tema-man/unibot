package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

class Unibot(
    relays: List<Relay>,
    private val router: Router,
    private val meter: MeterRegistry
) {

    private val log = KotlinLogging.logger("Unibot")
    private val relayScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mutex = Mutex()
    private val workingRelays = relays.toMutableList()

    fun isRunning() = relayScope.isActive

    fun start() = meter.timeOf("unibot.startup") {
        log.info { "Starting relays" }
        val iterator = workingRelays.iterator()
        while (iterator.hasNext()) {
            val relay = iterator.next()
            relayScope.launch { startRelay(relay) }
        }
    }

    fun stop() {
        val jobs = workingRelays.map { relay ->
            relayScope.async { relay.stop() }
        }
        relayScope.launch {
            jobs.awaitAll()
            relayScope.coroutineContext.cancelChildren()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun startRelay(relay: Relay) {
        val postingsFlow = relay.incomingPostingsFlow()
            .flatMapMerge { posting -> handle(posting) }
            .filterNotNull()
            .catch { cause ->
                log.error { "Exception occurred in relay ${relay.javaClass.name}. Cause: $cause" }
                relay.restart()
            }
            .onStart { log.info { "${relay.javaClass.simpleName} subscribed to postings" } }
            .flowOn(Dispatchers.IO)

        runCatching {
            relay.start()
            relay.afterStartSetup()
            postingsFlow.collect { posting -> relay.post(posting) }
        }.onFailure { cause ->
            log.error { "Failed to start relay ${relay.javaClass.name}. Cause: $cause. Relay will be disabled." }
            relay.stop()
            mutex.withLock {
                workingRelays.remove(relay)
                log.info { "Relay ${relay.javaClass.name} has been disabled." }
            }
        }
    }

    private fun handle(incoming: Posting): Flow<Posting> {
        val responder = router.pickResponder(incoming) ?: return emptyFlow()

        val startTime = System.currentTimeMillis()
        val respondTimer = meter.timer("unibot.response", "responder", responder.javaClass.name)
        return responder.responseStream(incoming)
            .onEach { posting ->
                log.info { "Responding with: $posting" }
            }
            .onCompletion {
                respondTimer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
            }
    }
}