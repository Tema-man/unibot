package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.telegram.text
import org.telegram.telegrambots.meta.api.objects.Update

class UrlsExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Posting.Content.Extra? {
        val urls = update.message.entities
            ?.filter { it.type == "url" }
            ?.mapNotNull { it.text }
            ?: return null

        return Posting.Content.Extra.Urls(urls = urls, text = update.text)
    }
}