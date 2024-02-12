package dev.cherryd.unibot.responder.help

import dev.cherryd.unibot.core.CommandDescription
import dev.cherryd.unibot.core.CommandResponder
import dev.cherryd.unibot.core.CommandsRepository

class CommandsRepositoryImpl constructor(
    private val commands: List<CommandResponder>
) : CommandsRepository {

    override fun getCommands(): List<CommandDescription> = commands.map { it.commandDescription }
}