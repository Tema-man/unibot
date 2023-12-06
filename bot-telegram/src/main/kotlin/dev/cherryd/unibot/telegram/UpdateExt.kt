package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.User
import org.telegram.telegrambots.meta.api.objects.Update

internal fun Update.getUniBotChat(): Chat {
    val tgChat = when {
        hasMessage() -> message.chat
        hasEditedMessage() -> editedMessage.chat
        else -> callbackQuery.message.chat
    }

    return Chat(
        id = tgChat.id.toString(),
        name = tgChat.title ?: tgChat.userName ?: "-",
        type = when {
            tgChat?.isGroupChat == true -> Chat.Type.GROUP
            tgChat?.isSuperGroupChat == true -> Chat.Type.SUPERGROUP
            else -> Chat.Type.PRIVATE
        }
    )
}

internal fun Update.toUser(): User {
    val user = when {
        hasMessage() -> message.from
        hasEditedMessage() -> editedMessage.from
        hasCallbackQuery() -> callbackQuery.from
        hasPollAnswer() -> pollAnswer.user
        hasPreCheckoutQuery() -> preCheckoutQuery.from
        else -> throw IllegalArgumentException("Cant process $this")
    }

    val formattedName = (user.firstName.let { "$it " }) + (user.lastName ?: "")
//        val isFromDeveloper = botConfig?.let { it.developer == user.userName } ?: false
    val role = when {
//            isFromDeveloper -> User.Role.DEVELOPER
        user.isBot -> User.Role.BOT
        else -> User.Role.USER
    }
    return User(
        id = user.id.toString(),
        role = role,
        name = formattedName,
    )
}



internal fun Update.toCommand(botName: String): String? = message?.getCommand(botName)