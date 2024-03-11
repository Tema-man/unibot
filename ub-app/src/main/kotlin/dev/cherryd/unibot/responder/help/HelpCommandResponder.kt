package dev.cherryd.unibot.responder.help

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.Phrase
import kotlinx.coroutines.flow.FlowCollector

class HelpCommandResponder(
    private val commandsRepository: CommandsRepository,
    private val dictionary: Dictionary
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "help",
        description = "Shows help",
        arguments = listOf(
            CommandDescription.Argument("command_name", "Shows help for specific command"),
        ),
        examples = listOf(
            "help # Prints this message",
            "help quote # Prints help for 'quote' command"
        )
    )

    override fun getPriority(settings: Settings) = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val args = incoming.content.extra.text.split(" ")
        val helpMessageText =
            if (args.isEmpty() || args.size < 2) printCommonHelp(incoming.settings)
            else printSpecificHelp(incoming.settings, args[1])

        flow.emit(incoming.textAnswer { helpMessageText })
    }

    private fun printCommonHelp(settings: Settings): String {
        val commandsList = commandsRepository.getCommands().joinToString("\n") { settings.commandPrefix + it.command + " - " + it.description }
        val helpDescription = commandDescription.let { settings.commandPrefix + it.command + " - " + it.description }

        return dictionary.getPhrase(Phrase.HELP, settings, "$commandsList\n$helpDescription")
    }

    private fun printSpecificHelp(settings: Settings, command: String): String {
        val commandDescription = commandsRepository.findCommandDescription(command)
        return commandDescription?.print(settings.commandPrefix) ?: dictionary.getPhrase(Phrase.COMMAND_NOT_FOUND, settings, command)
    }

    private val Settings.commandPrefix: String
        get() = bot.commandPrefix
}