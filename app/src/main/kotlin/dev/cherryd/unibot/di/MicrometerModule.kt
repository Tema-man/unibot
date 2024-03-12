package dev.cherryd.unibot.di

import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

object MicrometerModule {

    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val meterRegistry = CompositeMeterRegistry()
        .add(prometheusMeterRegistry)
        .add(SimpleMeterRegistry())
}