package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.core.command.UserNameArgumentParser
import dev.cherryd.unibot.core.random.TypingDelayGenerator
import dev.cherryd.unibot.data.Database
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.DictionaryParser
import dev.cherryd.unibot.media.YtDlpWrapper

object AppModule {

    val environment = Environment()

    val ytDlpWrapper = YtDlpWrapper(environment)

    val typingDelayGenerator = TypingDelayGenerator()

    val userNameArgumentParser = UserNameArgumentParser()

    val database = Database(
        environment = environment,
        metrics = MicrometerModule.meterRegistry
    )

    private val dictionaryParser = DictionaryParser()
    val dictionary = Dictionary(dictionaryParser)

}