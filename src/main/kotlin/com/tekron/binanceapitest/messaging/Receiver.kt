package com.tekron.binanceapitest.messaging

import com.tekron.binanceapitest.BinanceKlineProcessor
import com.tekron.binanceapitest.adapter.CoinSymbol
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sentry.Sentry
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@RabbitListener(queues = ["symbol-fetch-queue"])
@Component
class Receiver(
    private val processor: BinanceKlineProcessor
) {
    private val logger = KotlinLogging.logger {  }

    @RabbitHandler
    fun receive(message: String) {
        Sentry.captureMessage("Processing $message")
        processor.process(CoinSymbol.valueOf(message))
    }
}