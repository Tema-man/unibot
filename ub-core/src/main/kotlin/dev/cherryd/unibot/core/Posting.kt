package dev.cherryd.unibot.core

/**
 * The message produced in a context of a particular bot, and should have all the necessary data to send
 * a message to a chat.
 * Properties:
 * - message id
 * - user id
 * - chat id
 * - bot context (bot name, bot settings, user settings, chat settings) should it be in message model?
 * - message text (optional)
 * - extra (optional)
 *
 * the message may could contain several extras:
 * - an image
 * - a sticker
 * - a reaction
 * - a poll (🤔telegram only probably)
 * - a video (not used for now)
 * - an audio (not used for now)
 * - gps location (not used for now)
 * - something else... (not used for now)
 *
 * The message could be a reply to another message in that case we should also get the original message
 * The message could contain a command or a mention
 * The message could describe some chat action e.g:
 * - User added to/removed from a chat
 * - This bot added to/removed from a chat
 * - payments?... (not used for now)
 * - else?
 */
data class Posting(
    val content: Content,
    val settings: Settings
) {

    val extra: Content.Extra get() = content.extra

    data class Content(
        val id: String,
        val sender: User,
        val chat: Chat,
        val extra: Extra,
        val attachment: Attachment? = null,
    ) {
        sealed class Extra {
            data class Text(val text: String) : Extra()
            data class Command(val command: String, val text: String) : Extra()
            sealed class ChatEvent : Extra() {
                data class UserJoined(val user: User) : ChatEvent()
                data class UserLeft(val user: User) : ChatEvent()
                data object BotAdded : ChatEvent()
                data object BotRemoved : ChatEvent()
            }
        }

        sealed class Attachment {
            data class Reply(val replyContent: Content) : Attachment()
            data class Sticker(val stickerId: String) : Attachment()
        }
    }
}
