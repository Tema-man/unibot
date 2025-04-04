package dev.cherryd.unibot.logtools

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase


class LogsHighlighter : ForegroundCompositeConverterBase<ILoggingEvent>() {
    override fun getForegroundColorCode(event: ILoggingEvent): String = when (event.level) {
        Level.ERROR -> ANSIConstants.BOLD + ANSIConstants.RED_FG
        Level.WARN -> ANSIConstants.YELLOW_FG
        Level.INFO -> ANSIConstants.WHITE_FG
        Level.DEBUG -> ANSIConstants.GREEN_FG
        else -> ANSIConstants.DEFAULT_FG
    }
}