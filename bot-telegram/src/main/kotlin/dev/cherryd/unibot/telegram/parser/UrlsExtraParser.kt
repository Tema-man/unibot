package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.telegram.text
import org.telegram.telegrambots.meta.api.objects.Update

class UrlsExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Post.Extra? {
        val urls = update.message.entities
            ?.filter { it.type == "url" }
            ?.mapNotNull { it.text }
            ?: return null

        return Post.Extra.Urls(urls = urls, text = update.text)
    }
}