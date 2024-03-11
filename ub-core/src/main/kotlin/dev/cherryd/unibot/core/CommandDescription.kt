package dev.cherryd.unibot.core

data class CommandDescription(
    val command: String,
    val description: String,
    val arguments: List<Argument> = emptyList(),
    val examples: List<String> = emptyList()
) {

    data class Argument(
        val name: String,
        val description: String
    ) {
        fun describe() = "$name - $description"
    }

    fun describe(prefix: String) = "$prefix$command - $description"

    fun print(prefix: String) =
        """
$prefix$command [${arguments.joinToString(", ") { "<${it.name}>" }}]

Arguments:
${arguments.joinToString("\n") { " - ${it.describe()}" }}

Examples:
${examples.joinToString(separator = "\n") { " - $it" }}
""".trimIndent()
}