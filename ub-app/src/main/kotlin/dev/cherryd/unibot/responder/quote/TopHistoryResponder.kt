package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class TopHistoryResponder constructor(
    private val repository: QuoteRepository
) : CommandResponder() {

    override fun getPriority(settings: Settings) = Responder.Priority.MEDIUM

    override val commandDescription = CommandDescription(
        command = "top_history",
        description = "Sends a random curse",
        usage = """
            just call top_history for a random curse
            or call top_history @username to curse someone
        """.trimIndent()
    )

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val extra = (incoming.extra as? Posting.Content.Extra.Command) ?: return
        val curse = repository.getCurses().random()

        val params = extra.text.split(" ")
        val text = if (params.size > 1) {
            val username = params.getOrNull(1) ?: return
            if (!username.startsWith("@")) {
                "@$username, $curse"
            } else {
                "$username, $curse"
            }
        } else {
            curse
        }

        flow.emit(incoming.answer(Posting.Content.Extra.Text(text)))
    }
}