package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class QuoteResponder(
    private val quoteRepository: QuoteRepository
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "quote",
        description = "Send a random quote from the database",
        arguments = listOf(
            CommandDescription.Argument("tag", "Send a random quote from by specific tag")
        ),
        examples = listOf(
            "quote # Without an argument. Send a random quote",
            "quote love # Send a random quote by tag 'love'"
        )
    )

    override fun getPriority(settings: Settings) = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val quote = quoteRepository.getRandom()
        flow.emit(incoming.textAnswer { quote })
    }
}