package dev.cherryd.unibot.dictionary

import dev.cherryd.unibot.Unibot
import io.github.oshai.kotlinlogging.KotlinLogging
import org.tomlj.Toml
import org.tomlj.TomlParseResult

class DictionaryParser {

    private val logger = KotlinLogging.logger { }

    fun parseDictionary(): Map<Theme, Map<Phrase, String>> {
        val indexMap = mutableMapOf<Theme, Map<Phrase, String>>()
        for (theme in Theme.entries) {
            val toml = readTomlFromStatic(theme)
            if (toml == null) {
                logger.warn { "Theme $theme is missing, skipping" }
                continue
            }

            val phrases = mutableMapOf<Phrase, String>()
            for (phrase in Phrase.entries) {
                toml.getString(phrase.name)
                    ?.let { phrases[phrase] = it }
                    ?: logger.warn { "Phrase $phrase in $theme is missing" }
            }

            indexMap[theme] = phrases
            logger.info { "Theme $theme loaded" }
        }
        return indexMap
    }

    private fun readTomlFromStatic(theme: Theme): TomlParseResult? {
        val filename = "phrases${if (theme.key.isNotBlank()) "-${theme.key}" else ""}.toml"
        val resourceAsStream = Unibot::class.java.classLoader
            .getResourceAsStream("static/dictionary/$filename") ?: return null

        return Toml.parse(resourceAsStream)
    }
}