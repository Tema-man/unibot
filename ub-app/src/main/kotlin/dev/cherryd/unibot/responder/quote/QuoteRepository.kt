package dev.cherryd.unibot.responder.quote

import dev.cherryd.unibot.Unibot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.tomlj.Toml
import org.tomlj.TomlTable
import java.util.*

class QuoteRepository {

    private val quotes: MutableMap<String, List<String>>
    private val flattenQuotes: List<String>

    init {
        val rawArray = Toml.parse(readFileFromStatic("quotes.toml")).getArray("quotes")
            ?: throw IllegalStateException("quotes.toml is missing quotes array")

        quotes = rawArray.toList()
            .map { row -> map(row as TomlTable) }
            .groupBy(Pair<String, String>::first, Pair<String, String>::second)
            .toMutableMap()

        val mamoeb = readFileFromStatic("curses")
            .readAllBytes()
            ?.let { Base64.getMimeDecoder().decode(it) }
            ?.decodeToString()
            ?.let { Json.decodeFromString<Mamoeb>(it) }
            ?: throw IllegalStateException("curses is missing")

        quotes[MAMOEB] = mamoeb.curses.map { it }

        flattenQuotes = quotes.values.flatten()
    }

    private fun map(row: TomlTable): Pair<String, String> {
        val tag = row["tag"] as String?
        val quote = row["quote"] as String?
        if (tag == null || quote == null) {
            throw IllegalStateException("quotes.toml is invalid, current row is $row")
        }
        return tag to quote
    }

    fun getTags() = quotes.keys

    fun getByTag(tag: String) = quotes[tag]?.random()

    fun getRandom() = flattenQuotes.random()

    fun getCurses(): List<String> = quotes[MAMOEB] ?: emptyList()

    private fun readFileFromStatic(filename: String) = Unibot::class.java.classLoader
        .getResourceAsStream("static/$filename")
        ?: throw IllegalStateException("$filename is missing")


    @Serializable
    data class Mamoeb(
        @SerialName("Templates") val curses: List<String>,
    )

    companion object {
        private const val MAMOEB = "mamoeb"
    }
}