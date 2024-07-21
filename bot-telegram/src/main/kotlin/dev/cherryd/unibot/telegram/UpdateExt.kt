package dev.cherryd.unibot.telegram

import dev.cherryd.unibot.core.Chat
import dev.cherryd.unibot.core.Settings
import dev.cherryd.unibot.core.User
import org.telegram.telegrambots.meta.api.objects.InaccessibleMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User as TgUser

internal fun Update.getTgChat() = when {
    hasMessage() -> message.chat
    hasEditedMessage() -> editedMessage.chat
    else -> when (val message = callbackQuery.message) {
        is Message -> message.chat
        else -> (message as InaccessibleMessage).chat
    }
}

internal fun Update.getUniBotChat(chatSettings: Chat.Settings): Chat {
    val tgChat = getTgChat()
    return Chat(
        id = tgChat.id.toString(),
        name = tgChat.title ?: tgChat.userName ?: "-",
        type = when {
            tgChat?.isGroupChat == true -> Chat.Type.GROUP
            tgChat?.isSuperGroupChat == true -> Chat.Type.SUPERGROUP
            else -> Chat.Type.PRIVATE
        },
        settings = chatSettings
    )
}

internal fun Update.toUser(settings: Settings): User {
    val user = when {
        hasMessage() -> message.from
        hasEditedMessage() -> editedMessage.from
        hasCallbackQuery() -> callbackQuery.from
        hasPollAnswer() -> pollAnswer.user
        hasPreCheckoutQuery() -> preCheckoutQuery.from
        else -> throw IllegalArgumentException("Cant process $this")
    }
    val isAdmin = kotlin.runCatching {
        chatMember.newChatMember.status in setOf("administrator", "creator")
    }.getOrDefault(false)

    return user.toUser(settings, isAdmin)
}

internal fun TgUser.toUser(settings: Settings, isAdmin: Boolean = false): User {
    val role = when {
        settings.developerName == userName -> User.Role.DEVELOPER
        isBot -> User.Role.BOT
        isAdmin -> User.Role.ADMIN
        else -> User.Role.USER
    }
    return User(
        id = id.toString(),
        role = role,
        name = userName ?: "",
    )
}

internal val Update.text: String
    get() = message.text ?: ""