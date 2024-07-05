package dev.cherryd.unibot.responder.talking

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.random.RandomThreshold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ThreadLocalRandom

class HuificatorResponder : Responder {

    private val randomThreshold = RandomThreshold()

    override fun getPriority(settings: Settings) = Responder.Priority.MEDIUM

    override fun canHandle(posting: Posting): Boolean = shouldHuify(posting)

    override fun responseStream(incoming: Posting): Flow<Posting> {
        if (!canHandle(incoming)) return emptyFlow()
        return flow {
            val huified = huify(incoming.extra.text)
            emit(incoming.textAnswer { huified })
        }
    }

    private fun huify(word: String): String {
        val wordLowerCase = getLastWord(word).lowercase()
        val postfix = String(wordLowerCase.toCharArray().dropWhile { !VOWELS.contains(it) }.toCharArray())
        return when {
            postfix.isEmpty() -> "хуе" + wordLowerCase.drop(2)
            rules.containsKey(postfix[0]) -> "ху" + rules[postfix[0]] + postfix.drop(1).dropLastDelimiter()
            else -> "ху$postfix"
        }
    }

    private fun getLastWord(text: String) = text.split(regex = spaces).last()

    private fun shouldHuify(posting: Posting): Boolean {
        if (posting.extra.text.isBlank()) return false

        val wordLowerCase = getLastWord(posting.extra.text).lowercase()
        if (wordLowerCase.length < 5) return false
        if (english.matches(wordLowerCase)) return false
        if (nonLetters.matches(wordLowerCase.dropLast(wordLowerCase.length - 3))) return false
        if (onlyDashes.matches(wordLowerCase)) return false
        if (wordLowerCase.startsWith("ху", true)) return false

        return randomThreshold.checkRandom()
    }

    private fun String?.dropLastDelimiter(): String? {
        if (this.isNullOrEmpty()) return this
        return if (lastOrNull()?.isLetterOrDigit() != true) dropLast(1) else this
    }

    private companion object {
        const val VOWELS = "ёэоеаяуюыи"
        val rules = mapOf('о' to "ё", 'а' to "я", 'у' to "ю", 'ы' to "и", 'э' to "е")
        val nonLetters = Regex(".*[^a-я]+.*")
        val onlyDashes = Regex("^-*$")
        val english = Regex(".*[A-Za-z]+.*")
        val spaces = Regex("\\s+")
    }
}