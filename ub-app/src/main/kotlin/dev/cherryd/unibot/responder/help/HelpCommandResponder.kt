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
        val prefix = incoming.settings.bot.commandPrefix
        val message = "Available commands: \n\n" +
                "${commandsRepository.getCommands().joinToString("\n") { prefix + it.command + " - " + it.description }} \n" +
                commandDescription.let { prefix + it.command + " - " + it.description }

        flow.emit(incoming.answer(Posting.Content.Extra.Text(message)))
    }
}