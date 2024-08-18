package com.tekron.binanceapitest.model

import java.time.LocalDateTime

data class MarketDataRecord(
    val openTime: LocalDateTime,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val closeTime: LocalDateTime,
    val quoteVolume: Double,
    val count: Int,
    val takerBuyVolume: Double,
    val takerBuyQuoteVolume: Double,
    val ignore: Boolean
)