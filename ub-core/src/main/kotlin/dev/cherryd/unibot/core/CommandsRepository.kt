package dev.cherryd.unibot.core

interface CommandsRepository {
    fun getCommands(): List<CommandDescription>
}