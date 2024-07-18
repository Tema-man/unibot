package dev.cherryd.unibot.responder.pidor

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.data.PidorsRepository
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.Phrase
import kotlinx.coroutines.flow.FlowCollector

class RandomPidorResponder(
    private val pidorsRepository: PidorsRepository,
    private val usersRepository: UsersRepository,
    private val dictionary: Dictionary
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "pidor",
        description = "Выбирает случайного пидора",
        arguments = listOf(
            CommandDescription.Argument("user", "Имя пидора. Если пидор уже есть, то новый не будет выбран")
        ),
        examples = listOf(
            "/pidor",
            "/pidor @username"
        )
    )

    override fun getPriority(settings: Settings): Responder.Priority = Responder.Priority.HIGH

    override suspend fun handleCommand(flow: FlowCollector<Post>, post: Post) {
        val pidorOfTheDay = pidorsRepository.getPidorOfChat(post.chat)
        if (pidorOfTheDay != null) {
            flow.emit(dictionary.phraseAnswer(Phrase.PIROR_DISCOVERED_ONE, post, "@${pidorOfTheDay.name}"))
            return
        }

        val args = post.extra.text.split(" ")
        when {
            args.size == 2 -> flow.tagPidor(post, args[1])
            else -> flow.chooseRandomPidor(post)
        }
    }

    private suspend fun FlowCollector<Post>.chooseRandomPidor(post: Post) {
        val users = usersRepository.getUsersOfChat(post.chat, activeOnly = true)
        if (users.isEmpty()) {
            emit(post.textAnswer { "Нет активных пользователей в чате" })
            return
        }

        listOf(
            Phrase.PIDOR_SEARCH_START,
            Phrase.PIDOR_SEARCH_MIDDLE,
            Phrase.PIDOR_SEARCH_FINISHER,
        )
            .map { phrase -> dictionary.getPhrase(phrase, post.settings) }
            .forEach { phrase -> emit(post.textAnswer { phrase }) }

        val newPidor = users.random()
        pidorsRepository.savePidor(newPidor, post.chat)
        emit(post.textAnswer { "@${newPidor.name}" })
    }

    private suspend fun FlowCollector<Post>.tagPidor(post: Post, mention: String) {
        var user = usersRepository.findUserByName(mention.removePrefix("@"))
        if (user == null) {
            emit(post.textAnswer { "Кого ты пытаешься пидором называть, а?" })
            emit(post.textAnswer { "Тебе теперь и быть пидором!" })
            user = post.sender
        }
        val phrase = dictionary.getPhrase(Phrase.PIDOR_SEARCH_FINISHER, post.settings)
        emit(post.textAnswer { phrase })
        pidorsRepository.savePidor(user, post.chat)
        emit(post.textAnswer { "@${user.name}" })
    }
}