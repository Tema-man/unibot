package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import org.telegram.telegrambots.meta.api.objects.Update

class StickerExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Post.Extra? {
        val sticker = update.message.sticker ?: return null
        return Post.Extra.Sticker(stickerId = sticker.fileId)
    }
}