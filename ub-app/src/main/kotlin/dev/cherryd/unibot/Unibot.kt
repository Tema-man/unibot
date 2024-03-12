package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Unibot(
    relays: List<Relay>,
    private val router: Router
) {

    private val log = KotlinLogging.logger("Unibot")
    private val relayScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mutex = Mutex()
    private val workingRelays = relays.toMutableList()

    fun isRunning() = relayScope.isActive

    fun start() {
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
        log.info { "Received posting: $incoming" }
        val responder = router.pickResponder(incoming)
        log.info { "Picked responder: $responder" }
        return responder.responseStream(incoming)
    }
}