package dev.cherryd.unibot.responder.help

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class HelpCommandResponder(
    private val commandsRepository: CommandsRepository
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "help",
        description = "Shows help",
        usage = "help"
    )

    override fun getPriority(settings: Settings) = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val message = """
            Available commands:
            ${commandsRepository.getCommands().joinToString("\n") { "/" + it.command + " - " + it.description }}
            ${commandDescription.let { "/" + it.command + " - " + it.description }} 
        """.trimIndent()

        flow.emit(incoming.answer(Posting.Content.Extra.Text(message)))
    }
}