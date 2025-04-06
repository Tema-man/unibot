package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.core.command.UserNameArgumentParser
import kotlinx.coroutines.flow.FlowCollector

class TopHistoryResponder(
    private val userNameArgumentParser: UserNameArgumentParser,
    private val quoteRepository: QuoteRepository,
    private val usersRepository: UsersRepository
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
        val curse = quoteRepository.getCurses().random()
        val user = userNameArgumentParser.parse(extra.text)?.let { usersRepository.findUserByName(it) }
        val text = user?.let { "${user.mention}, $curse" } ?: curse
        flow.emit(post.textAnswer { text })
    }
}