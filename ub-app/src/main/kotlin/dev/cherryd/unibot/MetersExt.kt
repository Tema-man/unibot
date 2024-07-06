package dev.cherryd.unibot

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer

inline fun <T> MeterRegistry.timeOf(
    metric: String,
    crossinline process: Timer.ResourceSample.() -> T
): T = Timer.resource(this, metric).use { process(it) }