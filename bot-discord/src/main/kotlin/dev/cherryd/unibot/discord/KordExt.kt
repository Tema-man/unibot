package dev.cherryd.unibot.discord

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User as KordUser

fun MessageChannelBehavior.toChat(): Chat = Chat(
    id = id.value.toString(),
    name = mention,
    type = Chat.Type.GROUP
)

fun KordUser.toUser(): User = User(
    id = id.value.toString(),
    name = username,
    role = when {
        isBot -> User.Role.BOT
        else -> User.Role.USER
    }
)

fun UserData.toUser(): User = User(
    id = id.value.toString(),
    name = username,
    role = when {
        bot.discordBoolean -> User.Role.BOT
        else -> User.Role.USER
    }
)