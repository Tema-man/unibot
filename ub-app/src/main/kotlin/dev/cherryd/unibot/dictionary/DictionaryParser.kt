package dev.cherryd.unibot.dictionary

import dev.cherryd.unibot.Unibot
import io.github.oshai.kotlinlogging.KotlinLogging
import org.tomlj.Toml
import org.tomlj.TomlArray
import org.tomlj.TomlParseResult

class DictionaryParser {

    private val logger = KotlinLogging.logger { }

    fun parseDictionary(): Map<Theme, Map<Phrase, List<String>>> {
        val indexMap = mutableMapOf<Theme, Map<Phrase, List<String>>>()
        for (theme in Theme.entries) {
            val toml = readTomlFromStatic(theme)
            if (toml == null) {
                logger.warn { "Theme $theme is missing, skipping" }
                continue
            }

            val phrases = mutableMapOf<Phrase, List<String>>()
            for (phrase in Phrase.entries) {
                toml.getArray(phrase.name)?.asStringList()
                    ?.let { phrases[phrase] = it }
                    ?: logger.warn { "Phrase $phrase in $theme is missing" }
            }

            indexMap[theme] = phrases
            logger.info { "Theme $theme loaded" }
        }
        return indexMap
    }

    private fun TomlArray.asStringList(): List<String> = this.toList()
        ?.map(Any::toString)
        ?.map { line -> line.replace("\r", "") }
        ?: emptyList()

    private fun readTomlFromStatic(theme: Theme): TomlParseResult? {
        val filename = "phrases${if (theme.key.isNotBlank()) "-${theme.key}" else ""}.toml"
        val resourceAsStream = Unibot::class.java.classLoader
            .getResourceAsStream("static/dictionary/$filename") ?: return null

        return Toml.parse(resourceAsStream)
    }
}