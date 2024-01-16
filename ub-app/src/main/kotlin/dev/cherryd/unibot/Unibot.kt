package dev.cherryd.unibot

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Relay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

    private fun startRelayJob(relay: Relay) {
        relay.incomingPostingsFlow()
            .onEach { incoming ->
                val outgoing = handle(incoming)
                relay.post(outgoing)
            }
            .catch { cause ->
                log.error { "Exception occurred in relay ${relay.javaClass.name}. Cause: $cause" }
                relay.restart()
            }
            .launchIn(scope)

        relay.start()
    }

    private suspend fun handle(incoming: Posting): Posting {
        log.info { "Received posting: $incoming" }

        val processor = router.pickPostingTransformer(incoming)
        log.info { "Selected transformer: ${processor.javaClass.name}" }

        val outgoing = processor.transform(incoming)
        log.info { "Outgoing posting: $outgoing" }

        return outgoing
    }
}