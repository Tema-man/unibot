package dev.cherryd.unibot.processors.tiktok

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.PostingTransformer
import dev.cherryd.unibot.core.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.util.*

class TikTokVideoDownloader(
    environment: Environment
) : PostingTransformer {
    private val log = KotlinLogging.logger {}
    private val ytdlLocation = environment.get(YTDLP_LOCATION)

    override fun getPriority(settings: Settings): PostingTransformer.Priority =
        if (ytdlLocation.isBlank()) PostingTransformer.Priority.DISABLED
        else PostingTransformer.Priority.MEDIUM

    override suspend fun transform(incoming: Posting): Posting? {
        val extra = incoming.extra
        if (extra !is Posting.Content.Extra.Urls) return null

        val urls = extra.urls.filter { it.canHandle() }
        if (urls.isEmpty()) return null

        val extras = urls.map { url ->
            val downloadedFile = download(url)
            Posting.Content.Extra.Video(downloadedFile)
        }
        return incoming.answer(Posting.Content.Extra.Composite(extras))
    }

    private fun download(url: String): File {
        val filename = "${UUID.randomUUID()}.mp4"
        val process = ProcessBuilder(ytdlLocation, url, "-o", filename).start()
        log.info { "Running yt-dlp..." }
        process.inputStream.reader(Charsets.UTF_8).use {
            log.info { it.readText() }
        }
        process.waitFor()
        log.info { "Finished running yt-dlp" }
        return File(filename)
    }

    private fun String.canHandle(): Boolean =
        contains("instagram.com/reel", ignoreCase = true) || contains("tiktok", ignoreCase = true)

    companion object {
        private const val YTDLP_LOCATION = "YTDLP_LOCATION"
    }
}