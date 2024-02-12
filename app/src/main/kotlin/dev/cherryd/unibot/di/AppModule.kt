package dev.cherryd.unibot.di

import dev.cherryd.unibot.core.Environment
import dev.cherryd.unibot.media.YtDlpWrapper

object AppModule {

    val environment = Environment()

    val ytDlpWrapper = YtDlpWrapper(environment)

}