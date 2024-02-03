package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Posting
import dev.cherryd.unibot.core.Settings
import org.telegram.telegrambots.meta.api.objects.Update

class StickerExtraParser : ExtraParser {
    override fun parse(update: Update, settings: Settings): Posting.Content.Extra? {
        val sticker = update.message.sticker ?: return null
        return Posting.Content.Extra.Sticker(stickerId = sticker.fileId)
    }
}