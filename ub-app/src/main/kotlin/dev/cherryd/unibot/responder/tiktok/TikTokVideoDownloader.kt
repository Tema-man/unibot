package dev.cherryd.unibot.responder.tiktok

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Responder
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.media.YtDlpWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class TikTokVideoDownloader(
    private val ytDlpWrapper: YtDlpWrapper
) : Responder {

    override fun getPriority(settings: Settings): Responder.Priority =
        if (!ytDlpWrapper.available()) Responder.Priority.DISABLED
        else Responder.Priority.LOW

    override fun canHandle(post: Post): Boolean {
        val extra = post.extra
        if (extra !is Post.Extra.Urls) return false
        return extra.urls.any { it.canHandleUrl() }
    }

    override fun responseStream(post: Post): Flow<Post> = flow {
        val extra = (post.extra as? Post.Extra.Urls) ?: return@flow

        val urls = extra.urls.filter { it.canHandleUrl() }
        if (urls.isEmpty()) return@flow

        val extras = urls.mapNotNull { url ->
            emit(post.answer(Post.Extra.ChatEvent.SendingVideo))
            val downloadedFile = download(url) ?: return@mapNotNull null
            emit(post.answer(Post.Extra.Video(downloadedFile)))
        }
        if (extras.isEmpty()) return@flow
    }

    private fun download(url: String): File? = runCatching { ytDlpWrapper.downloadVideo(url) }.getOrNull()

    private fun String.canHandleUrl(): Boolean =
        isTikTok(this) || isVk(this) || isIG(this)

    private fun isTikTok(text: String) = text.contains("tiktok", ignoreCase = true)

    private fun isVk(text: String) = text.contains("vk.com/clip", ignoreCase = true)

    private fun isIG(text: String) = text.contains("instagram.com/reel", ignoreCase = true)
}