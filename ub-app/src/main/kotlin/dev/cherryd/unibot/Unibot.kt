package dev.cherryd.unibot

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Relay
import dev.cherryd.unibot.core.interceptor.BotInteractor
import dev.cherryd.unibot.core.interceptor.PostInterceptor
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class Unibot(
    relays: List<Relay>,
    private val router: Router,
    private val meter: MeterRegistry,
    private val interceptors: List<PostInterceptor>
) {

    private val log = KotlinLogging.logger("Unibot")
    private val relayScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mutex = Mutex()
    private val workingRelays = relays.toMutableList()

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

    private suspend fun startRelay(relay: Relay) {
        val postingsFlow = relay.incomingPostingsFlow()
            .flatMapMerge { post ->
                intercept(post, relay.interactor)
                handle(post)
            }
            .filterNotNull()
            .catch { cause ->
                log.error(cause) { "Exception occurred in relay ${relay.javaClass.name}. Cause: $cause" }
                relay.restart()
            }
            .onStart { log.info { "${relay.javaClass.simpleName} subscribed to postings" } }
            .flowOn(Dispatchers.IO)

        runCatching {
            relay.start()
            relay.afterStartSetup()
            postingsFlow.collect { posting -> relay.post(posting) }
        }.onFailure { cause ->
            log.error(cause) { "Failed to start relay ${relay.javaClass.name}. Cause: $cause. Relay will be disabled." }
            relay.stop()
            mutex.withLock {
                workingRelays.remove(relay)
                log.info { "Relay ${relay.javaClass.name} has been disabled." }
            }
        }
    }

    private fun handle(post: Post): Flow<Post> {
        val responder = router.pickResponder(post) ?: return emptyFlow()
        val startTime = System.currentTimeMillis()
        val respondTimer = meter.timer("unibot.response", "responder", responder.javaClass.name)
        return responder.responseStream(post)
            .onEach { posting ->
                log.info { "Responding with: $posting" }
            }
            .onCompletion {
                respondTimer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS)
            }
    }

    private suspend fun intercept(post: Post, interactor: BotInteractor) {
        interceptors.forEach { interceptor ->
            interceptor.intercept(post, interactor)
        }
    }
}