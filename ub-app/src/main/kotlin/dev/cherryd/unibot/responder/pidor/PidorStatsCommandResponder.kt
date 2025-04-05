package dev.cherryd.unibot.responder.pidor

import dev.cherryd.unibot.core.*
import dev.cherryd.unibot.core.command.UserNameArgumentParser
import dev.cherryd.unibot.data.PidorsRepository
import kotlinx.coroutines.flow.FlowCollector
import java.time.format.DateTimeFormatter

class PidorStatsCommandResponder(
    private val usersRepository: UsersRepository,
    private val pidorsRepository: PidorsRepository,
    private val userNameArgumentParser: UserNameArgumentParser
) : CommandResponder() {

    override val commandDescription = CommandDescription(
        command = "pidor_stats",
        description = "Показывает статистику пидора",
        arguments = listOf(
            CommandDescription.Argument("имя_пидора", "Показывает статистику для указанного пидора")
        ),
        examples = listOf(
            "pidor_stats # Показывает статистику всех пидоров",
            "pidor_stats @username # Показывает статистику для указанного пидора"
        )
    )

    override fun getPriority(settings: Settings): Responder.Priority = Responder.Priority.MEDIUM

    override suspend fun handleCommand(flow: FlowCollector<Post>, post: Post) {
        val pidorName = userNameArgumentParser.parse(post.extra.text)
        val message = if (pidorName != null) {
            getUserPidorStats(pidorName, post.chat)
        } else {
            getChatStatsMessage(post.chat)
        }
        flow.emit(post.textAnswer { message })
    }

    private suspend fun getChatStatsMessage(chat: Chat): String {
        val pidors = pidorsRepository.getPidorsLeaderboard(chat)
        if (pidors.isEmpty()) return "Нет пидоров в чате"

        val statsMessage = pidors.entries.joinToString("\n") { (user, count) ->
            "@${user.name} был пидором $count раз(а)"
        }
        return statsMessage
    }

    private suspend fun getUserPidorStats(userName: String, chat: Chat): String {
        val user = usersRepository.findUserByName(userName) ?: return "@${userName} ещё не был пидором"
        val pidor = pidorsRepository.getPidorRecordsForUser(user, chat) ?: return "@${userName} ещё не был пидором"

        val (count, lastDate) = pidor
        val lastDateFormatted = lastDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
        val statsMessage = "@${user.name} был пидором $count раз(а). Последний раз - $lastDateFormatted"
        return statsMessage
    }
}