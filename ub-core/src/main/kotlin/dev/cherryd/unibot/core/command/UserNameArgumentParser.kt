package dev.cherryd.unibot.core.command

class UserNameArgumentParser {

    fun parse(args: String): String? {
        val params = args.split(" ")
        val username = params.getOrNull(1) ?: return null
        return username.removePrefix("@")
    }
}