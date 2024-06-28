package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.core.*
import kotlinx.coroutines.flow.FlowCollector

class QuoteResponder(
    private val quoteRepository: QuoteRepository
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "quote",
        description = "Отправляет случайную цитату",
        arguments = listOf(
            CommandDescription.Argument("tag", "Отправляет случайную цитату по указаному тегу")
        ),
        examples = listOf(
            "quote # Отправляет случайную цитату",
            "quote $ARG_TAGS # Отправляет случайную цитату по тегу 'love'",
            "quote tags # Отправляет список всех тегов"
        )
    )

    override fun getPriority(settings: Settings) = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Posting>, incoming: Posting) {
        val message = when (val arg = incoming.content.extra.text.split(" ").getOrNull(1)) {
            ARG_TAGS -> quoteRepository.getTags().joinToString(", ")
            null -> quoteRepository.getRandom()
            else -> quoteRepository.getByTag(arg) ?: "Цитаты по тегу '$arg' не найдено"
        }
        flow.emit(incoming.textAnswer { message })
    }

    companion object {
        const val ARG_TAGS = "tags"
    }
}