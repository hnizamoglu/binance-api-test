package com.tekron.binanceapitest.strategy

import com.tekron.binanceapitest.model.MarketDataRecord
import java.time.LocalDateTime

interface TradingStrategy {
    fun execute(data: MarketDataRecord): TradingStrategyResult?
}

data class TradingStrategyResult(
    val detectedAt: LocalDateTime,
    val indexPrice: Double,
    val tradeDirection: TradeDirection,
    val targetPrice: Double,
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closePrice: Double,
)

enum class TradeDirection {
    SHORT, LONG
}