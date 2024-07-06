package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class TopHistoryResponder(
    private val repository: QuoteRepository
) : CommandResponder() {

    override fun getPriority(settings: Settings) = Responder.Priority.MEDIUM

    override val commandDescription = CommandDescription(
        command = "top_history",
        description = "Sends a random curse",
        arguments = listOf(
            CommandDescription.Argument("mention", "Curse the user with a random curse")
        ),
        examples = listOf(
            "top_history # Sends a random curse",
            "top_history @username # Curse the user with a random curse"
        )
    )

    override suspend fun handleCommand(flow: FlowCollector<Post>, incoming: Post) {
        val extra = (incoming.extra as? Post.Extra.Command) ?: return
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

        flow.emit(incoming.textAnswer { text })
    }
}