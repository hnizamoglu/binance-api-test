package com.tekron.binanceapitest.simulation

import com.tekron.binanceapitest.model.MarketDataRecord
import com.tekron.binanceapitest.strategy.TradeDirection
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

class TradeEngine(
    val buyRecords: List<MarketDataRecord>,
    val sellRecords: List<MarketDataRecord>,
) {
    private val logger = KotlinLogging.logger {  }
    //    private val multipliers = listOf(1.1,1.09,1.08,1.07,1.06,1.05)
    private val multipliers = listOf(1.10)
    private val profitThreshold = 0.10
    private val lossThreshold = 0.10
    fun work(): List<BuyPoint> {
        var totalLongMatch = 0
        var totalShortMatch = 0
        var totalLongFail = 0
        var totalShortFail = 0
        var totalMatch = 0
        var totalFail = 0

        val buyPoints = TradeDirection.entries.map { tradeDirection ->
            workOnTradeDirection(tradeDirection)
        }.flatten()
        buyPoints.forEach {
            totalMatch += it.match
            totalFail += it.fail
        }
        logger.info { "Overall: ${totalMatch-totalFail}" }

        return buyPoints
    }

    private fun workOnTradeDirection(tradeDirection: TradeDirection): List<BuyPoint> {
        logger.info { tradeDirection.name }
        return multipliers.map {
            workOnMultiplier(it, tradeDirection)
        }.flatten()
    }

    private fun workOnMultiplier(it: Double, tradeDirection: TradeDirection): List<BuyPoint> {
        var match = 0
        var fail = 0
        val buyPoints = extractBuyPoints(it, tradeDirection)
        buyPoints.forEach {
            calculateMaxMinEdges(it, tradeDirection)
            match += it.match
            fail += it.fail
        }

        val successRate = if (buyPoints.isNotEmpty()) ((match.toDouble() / buyPoints.size) * 100.0).roundToInt() else 0
        logger.info { "multiplier: $it buyPoints: ${buyPoints.size} match: $match <-> fail: $fail success rate: $successRate%" }
        return buyPoints
    }

    fun extractBuyPoints(
        multiplier: Double,
        direction: TradeDirection
    ): List<BuyPoint> {
        return when(direction) {
            TradeDirection.LONG -> extractLongBuyPoints(multiplier)
            TradeDirection.SHORT -> extractShortBuyPoints(multiplier)
        }

    }

    private fun extractShortBuyPoints(multiplier: Double) = buyRecords.filter {
        it.open * multiplier < it.high
    }.map {
        val buyPrice = it.open * multiplier
        var fail = 0
//        sellRecords.find { sellRecord -> sellRecord.openTime == it.openTime }.let {
//            fail = if(buyPrice * (1 + lossThreshold) < it!!.high) 1 else 0
//        }

        BuyPoint(
            time = it.openTime,
            openPrice = it.open,
            closePrice = it.close,
            buyPrice = buyPrice,
            multiplier = multiplier,
            highPrice = it.high,
            lowPrice = it.low,
            fail = fail
        )
    }

    private fun extractLongBuyPoints(multiplier: Double) = buyRecords.filter {
        it.open > it.low * multiplier
    }.map {
        val buyPrice = it.open * (2.0 - multiplier)
        var fail = 0
//        sellRecords.find { sellRecord -> sellRecord.openTime == it.openTime }.let {
//            fail = if(buyPrice * (1 - lossThreshold) > it!!.low) 1 else 0
//        }

        BuyPoint(
            time = it.openTime,
            openPrice = it.open,
            closePrice = it.close,
            buyPrice = buyPrice,
            multiplier = multiplier,
            highPrice = it.high,
            lowPrice = it.low,
            fail = fail
        )
    }

    fun calculateMaxMinEdges(buyPoint: BuyPoint, tradeDirection: TradeDirection) {
        if (buyPoint.fail == 1) return
        val iter = sellRecords.filter {
            it.openTime > buyPoint.time
        }.sortedBy { it.openTime }.iterator()

        when (tradeDirection) {
            TradeDirection.LONG -> calculateMaxMinEdgesForLong(buyPoint, iter)
            TradeDirection.SHORT -> calculateMaxMinEdgesForShort(buyPoint, iter)
        }
    }

    private fun calculateMaxMinEdgesForShort(buyPoint: BuyPoint, iter: Iterator<MarketDataRecord>) {
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.low < buyPoint.buyPrice * (1 - profitThreshold)) {
                buyPoint.match()
                buyPoint.marketDataRecord = next
                buyPoint.duration = Duration.between(buyPoint.time, next.openTime)
                break
            } else if(next.high > buyPoint.buyPrice * (1 + lossThreshold)) {
                buyPoint.fail()
                buyPoint.marketDataRecord = next
                buyPoint.duration = Duration.between(buyPoint.time, next.openTime)
                break
            }
        }
    }

    private fun calculateMaxMinEdgesForLong(buyPoint: BuyPoint, iter: Iterator<MarketDataRecord>) {
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.high > buyPoint.buyPrice * (1 + profitThreshold)) {
                buyPoint.match()
                buyPoint.marketDataRecord = next
                buyPoint.duration = Duration.between(buyPoint.time, next.openTime)
                break
            } else if(next.low * (1 + lossThreshold) < buyPoint.buyPrice) {
                buyPoint.fail()
                buyPoint.marketDataRecord = next
                buyPoint.duration = Duration.between(buyPoint.time, next.openTime)
                break
            }
        }
    }
}

data class BuyPoint(
    val time: LocalDateTime,
    val tradeDirection: TradeDirection? = null,
    val buyPrice: Double,
    val targetPrice: Double = 0.0,
    val multiplier: Double,
    var match: Int = 0,
    var fail: Int = 0,
    val isSuccess: Boolean? = null,
    val openPrice: Double,
    val closePrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    var duration: Duration? = null,
    var marketDataRecord: MarketDataRecord? = null,
    var sellRecord: MarketDataRecord? = null
) {
    fun match() = ++match
    fun fail() = ++fail
}

