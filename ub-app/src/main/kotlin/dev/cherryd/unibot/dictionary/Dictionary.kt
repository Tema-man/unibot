package dev.cherryd.unibot.dictionary

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * This class will have a list of different phrases and themes that can affect phrase content.
 * Themes is basically different configurations, languages, holidays.
 * Each phrase has a unique id, and a list of different translations.
 * User should be able to get phrase by id and theme.
 */
class Dictionary(
    dictionaryParser: DictionaryParser
) {

    private val logger = KotlinLogging.logger { }
    private val dictionary: Map<Theme, Map<Phrase, List<String>>> = dictionaryParser.parseDictionary()

    init {
        checkDefaults()
    }

    fun phraseAnswer(phrase: Phrase, posting: Posting) = posting.textAnswer { getPhrase(phrase, posting.settings) }

    fun getPhrase(phrase: Phrase, settings: Settings): String {
        val theme = selectTheme(settings)
        return getPhrase(phrase, theme)
    }

    fun getPhrase(phrase: Phrase, settings: Settings, vararg args: String): String {
        val theme = selectTheme(settings)
        return getPhrase(phrase, theme).format(*args)
    }

    private fun selectTheme(settings: Settings): Theme = Theme.DEFAULT

    private fun getPhrase(phrase: Phrase, theme: Theme): String {
        val phrasesByTheme = getDictionaryForTheme(theme)
        return phrasesByTheme[phrase]?.random() ?: getDefaultPhrase(phrase).random().also { logger.warn { "Phrase $phrase in $theme is missing" } }
    }

    private fun getDictionaryForTheme(theme: Theme): Map<Phrase, List<String>> =
        dictionary[theme] ?: throw IllegalStateException("Theme $theme is missing")

    private fun getDefaultPhrase(phrase: Phrase): List<String> = dictionary[Theme.DEFAULT]
        ?.get(phrase)
        ?: throw IllegalStateException("Default value for phrase $phrase is missing")

    private fun checkDefaults() {
        val missingDefaultPhrases = Phrase.entries.asSequence()
            .map { phrase -> phrase to dictionary[Theme.DEFAULT]?.get(phrase) }
            .filter { it.second == null }
            .map { it.first }
            .toList()

        if (missingDefaultPhrases.isNotEmpty()) {
            throw IllegalStateException("Some dictionary defaults missing. Check $missingDefaultPhrases")
        }
    }
}