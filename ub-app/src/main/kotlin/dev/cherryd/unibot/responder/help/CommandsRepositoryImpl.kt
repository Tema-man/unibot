package dev.cherryd.unibot.responder.help

import dev.cherryd.unibot.core.CommandDescription
import dev.cherryd.unibot.core.CommandResponder
import dev.cherryd.unibot.core.CommandsRepository

class CommandsRepositoryImpl(
    private val commands: List<CommandResponder>
) : CommandsRepository {

    private val index = commands.associateBy { it.commandDescription.command }

    override fun getCommands(): List<CommandDescription> = commands.map { it.commandDescription }

    override fun findCommandDescription(command: String): CommandDescription? = index[command]?.commandDescription
}