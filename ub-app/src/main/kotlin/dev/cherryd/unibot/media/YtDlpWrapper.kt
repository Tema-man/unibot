package dev.cherryd.unibot.media

import dev.cherryd.unibot.core.Environment
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.minutes

class YtDlpWrapper(
    environment: Environment
) {
    private val log = KotlinLogging.logger { }
    private val ytdlLocation = environment.get(YTDLP_LOCATION)
    private val fileDeleteScope = CoroutineScope(Dispatchers.Default)

    fun available(): Boolean = ytdlLocation.isNotBlank()
    fun downloadVideo(url: String, vararg params: String): File {
        return download("${UUID.randomUUID()}.%(ext)s", url, *params)
    }

    fun downloadAudio(url: String, vararg params: String): File {
        return download("%(title)s.%(ext)s", url, "-x", "--audio-format", "mp3", "--no-playlist", *params)
    }

    private fun download(filename: String, url: String, vararg params: String): File {
        log.info { "Running yt-dlp..." }
        val folderName = UUID.randomUUID().toString()
        val folder = File("/tmp/", folderName).apply { mkdir() }
        val process = ProcessBuilder(ytdlLocation, url, "-o", "\"${folder.absolutePath}/$filename\"", *params).start()
        process.inputStream.reader(Charsets.UTF_8).use { log.info { it.readText() } }
        process.errorStream.reader(Charsets.UTF_8).use { log.error { it.readText() } }
        process.waitFor()
        log.info { "Finished running yt-dlp" }

        val downloadedFile = folder.listFiles()?.firstOrNull() ?: throw IllegalStateException("yt-dlp failed to extract data")
        downloadedFile.deleteOnExit()
        fileDeleteScope.launch {
            runCatching {
                delay(5.minutes)
                downloadedFile.delete()
                log.info { "${downloadedFile.absolutePath} is deleted" }
            }.onFailure {
                log.error(it) { "Could not delete ${downloadedFile.absolutePath}" }
            }
        }
        return downloadedFile
    }

    companion object {
        private const val YTDLP_LOCATION = "YTDLP_LOCATION"
    }
}