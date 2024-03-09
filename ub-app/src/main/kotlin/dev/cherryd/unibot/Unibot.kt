package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Unibot(
    private val relays: List<Relay>,
    private val router: Router
) {

    private val log = KotlinLogging.logger {}

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun start() {
        relays.forEach { relay -> startRelayJob(relay) }
    }

    fun stop() {
        relays.forEach { it.stop() }
        scope.coroutineContext.cancelChildren()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun startRelayJob(relay: Relay) = runCatching {
        relay.incomingPostingsFlow()
            .flatMapMerge { posting -> handle(posting) }
            .filterNotNull()
            .onEach { posting -> relay.post(posting) }
            .catch { cause ->
                log.error { "Exception occurred in relay ${relay.javaClass.name}. Cause: $cause" }
                relay.restart()
                log.error { "Relay ${relay.javaClass.name} restarted." }
            }
            .launchIn(scope)

        relay.start()
        relay.afterStartSetup()
    }.onFailure { cause ->
        log.error { "Failed to start relay ${relay.javaClass.name}. Cause: $cause" }
    }

    private fun handle(incoming: Posting): Flow<Posting> {
        log.info { "Received posting: $incoming" }
        val responder = router.pickResponder(incoming)
        log.info { "Picked responder: $responder" }
        return responder.responseStream(incoming)
    }
}