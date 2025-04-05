package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.core.command.UserNameArgumentParser
import kotlinx.coroutines.flow.FlowCollector

class TopHistoryResponder(
    private val repository: QuoteRepository,
    private val userNameArgumentParser: UserNameArgumentParser
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

    override suspend fun handleCommand(flow: FlowCollector<Post>, post: Post) {
        val extra = (post.extra as? Post.Extra.Command) ?: return
        val curse = repository.getCurses().random()
        val userName = userNameArgumentParser.parse(extra.text)
        val text = userName?.let { "@$userName, $curse" } ?: curse
        flow.emit(post.textAnswer { text })
    }
}