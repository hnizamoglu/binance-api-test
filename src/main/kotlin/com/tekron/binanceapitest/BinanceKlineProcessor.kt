package com.tekron.binanceapitest

import com.tekron.binanceapitest.adapter.BinanceDataDownloader
import com.tekron.binanceapitest.adapter.CoinSymbol
import com.tekron.binanceapitest.notification.NotificationService
import com.tekron.binanceapitest.notification.TradeNotificationMessage
import com.tekron.binanceapitest.parser.BinanceDataParser
import com.tekron.binanceapitest.strategy.TradingStrategy
import com.tekron.binanceapitest.strategy.TradingStrategyResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class BinanceKlineProcessor(
    private val dataDownloader: BinanceDataDownloader,
    private val dataParser: BinanceDataParser,
    private val strategy: TradingStrategy,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {  }
    private val alreadyProcessedTicks = mutableMapOf<String, Boolean>()

    fun process(symbol: CoinSymbol) {
        logger.info { "processing ${symbol.name}" }
        val resp = dataDownloader.getRecentKline(symbol)
        resp?.let{
            val lst = dataParser.parse(resp)
            logger.debug { lst }
            lst.forEach {
                strategy.execute(it)?.let { result ->
                    val key = "${result.openTime}-${result.closeTime}-$symbol"
                    if(!alreadyProcessedTicks.containsKey(key)){
                        logger.info { "Found something!" }
                        logger.info { result }
                        sendNotification(result, symbol)
                        alreadyProcessedTicks[key] = true
                    }

                }
            }
        } ?: logger.info { "empty response" }
    }

    private fun sendNotification(result: TradingStrategyResult, symbol: CoinSymbol) {
        val notificationMessage = TradeNotificationMessage(
            date = result.toString(),
            symbol = symbol.name,
            direction = result.tradeDirection.name,
            currentPrice = result.indexPrice.toString(),
            targetPrice = result.targetPrice.toString()

        )
        notificationService.notify(notificationMessage)
    }
}