package com.tekron.binanceapitest

import com.tekron.binanceapitest.adapter.CoinSymbol
import com.tekron.binanceapitest.messaging.Sender
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.stereotype.Component

@Component
@Profile("orchestrator")
class BinanceKlineScheduler(
    private val messageSender: Sender,
) {
    private val logger = KotlinLogging.logger{ }

    @Scheduled(fixedDelay = 10_000)
    fun fetchKlineData() {
        logger.info { "kline schedule" }
        CoinSymbol.entries.forEach {
            messageSender.sendMessage(it.name)
        }
    }
}