package dev.cherryd.unibot.telegram

import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message

internal fun Message.getCommand(botName: String): String? {
    val entities = entities ?: return null
    for (entity in entities) {
        if (entity.offset == 0 && entity.type == EntityType.BOTCOMMAND) {
            val parts = entity.text.split("@")

            if (parts.size == 1) return parts[0]
            if (parts[1] == botName) return parts[0]
        }
    }
    return null
}