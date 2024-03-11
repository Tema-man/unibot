package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.dictionary.Dictionary
import dev.cherryd.unibot.dictionary.DictionaryParser
import dev.cherryd.unibot.media.YtDlpWrapper

object AppModule {

    val environment = Environment()

    val ytDlpWrapper = YtDlpWrapper(environment)

    private val dictionaryParser = DictionaryParser()
    val dictionary = Dictionary(dictionaryParser)

}