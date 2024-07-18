@file:UseSerializers(LocalDateSerializer::class)

package dev.cherryd.unibot.core

import dev.cherryd.unibot.core.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

data class Settings(
    val bot: Bot,
    val chat: Chat
) {
    data class Bot(
        val id: String,
        val name: String,
        val aliases: List<String>,
        val token: String,
        val developerName: String,
        val commandPrefix: String
    )

    @Serializable
    data class Chat(
        val lastSyncDate: LocalDate?
    ) {
        companion object {
            val DEFAULT = Chat(lastSyncDate = null)
        }
    }
}