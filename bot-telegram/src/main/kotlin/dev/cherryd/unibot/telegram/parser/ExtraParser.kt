package dev.cherryd.unibot.telegram.parser

import dev.cherryd.unibot.core.Post
import dev.cherryd.unibot.core.Settings
import org.telegram.telegrambots.meta.api.objects.Update

interface ExtraParser {
    fun parse(update: Update, settings: Settings): Post.Extra?
}