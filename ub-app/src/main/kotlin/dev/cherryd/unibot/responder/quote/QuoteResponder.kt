package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class QuoteResponder(
    private val quoteRepository: QuoteRepository
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "quote",
        description = "Send a random quote from the database",
        usage = "quote"
    )

    override fun getPriority(settings: Settings) = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val quote = quoteRepository.getRandom()
        flow.emit(incoming.answer(Posting.Content.Extra.Text(quote)))
    }
}