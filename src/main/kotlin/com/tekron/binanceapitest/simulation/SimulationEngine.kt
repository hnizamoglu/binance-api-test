package com.tekron.binanceapitest.simulation

import com.binance.connector.client.impl.spot.Trade
import com.tekron.binanceapitest.model.MarketDataRecord
import com.tekron.binanceapitest.strategy.TradeDirection
import io.github.oshai.kotlinlogging.KotlinLogging

class SimulationEngine(
    val buyRecords: List<MarketDataRecord>,
    val sellRecords: List<MarketDataRecord>,
    val multiplier: Double = 1.1,
    val profitThreshold: Double = 0.1,
    val lossThreshold: Double = 0.1,
) {
    private val logger = KotlinLogging.logger {  }

    fun work(): List<BuyPoint> {
        logger.info { "starting simulation. buy records size: ${buyRecords.size}" }
        return buyRecords.mapNotNull { dataRecord ->
            computeTradeDirection(dataRecord.open, dataRecord.high, dataRecord.low)?.let { direction ->
                val price = computePrice(dataRecord.open, direction)
                val targetPrice = computeTargetPrice(price, direction)
                val tradePoint = checkSuccess(price, dataRecord, direction)
                BuyPoint(
                    time = dataRecord.openTime,
                    tradeDirection = direction,
                    buyPrice = price,
                    targetPrice = targetPrice,
                    multiplier = multiplier,
                    isSuccess = tradePoint.isSuccess,
                    openPrice = dataRecord.open,
                    closePrice = dataRecord.close,
                    highPrice = dataRecord.high,
                    lowPrice = dataRecord.low,
                    duration = null,
                    marketDataRecord = dataRecord,
                    sellRecord = tradePoint.point
                )
            }
        }
    }

    fun computeTargetPrice(price: Double, direction: TradeDirection): Double {
        return when(direction) {
            TradeDirection.SHORT -> price * (1 - profitThreshold)
            TradeDirection.LONG -> price * (1 + profitThreshold)
        }
    }

    fun checkSuccess(price: Double, marketDataRecord: MarketDataRecord, tradeDirection: TradeDirection): TradePoint {
        val relevantHistoricalDataIter =
            sellRecords.filter {
                it.openTime > marketDataRecord.openTime
            }.sortedBy { it.openTime }.iterator()
        return when (tradeDirection) {
            TradeDirection.SHORT -> checkShortSuccess(price,  relevantHistoricalDataIter)
            TradeDirection.LONG -> checkLongSuccess(price, relevantHistoricalDataIter)
        }
    }

    private fun checkLongSuccess(
        price: Double,
        relevantHistoricalDataIter: Iterator<MarketDataRecord>
    ): TradePoint {
        while (relevantHistoricalDataIter.hasNext()) {
            val nextData = relevantHistoricalDataIter.next()
            if(nextData.high > price * (1 + profitThreshold)) {
                return TradePoint(
                    isSuccess = true,
                    point = nextData
                )
            } else if(nextData.low < price * (1 - lossThreshold)){
                return TradePoint(
                    isSuccess = false,
                    point = nextData
                )
            }
        }
        // todo: still holding, need to take care
        return TradePoint(false)
    }

    private fun checkShortSuccess(
        price: Double,
        relevantHistoricalDataIter: Iterator<MarketDataRecord>
    ): TradePoint {
        while (relevantHistoricalDataIter.hasNext()) {
            val nextData = relevantHistoricalDataIter.next()
            if(nextData.low < price * (1 - profitThreshold)){
                return TradePoint(
                    isSuccess = true,
                    point = nextData
                )
            } else if(nextData.high > price * (1 + lossThreshold)){
                return TradePoint(
                    isSuccess = false,
                    point = nextData
                )
            }
        }
        // todo: still holding, need to take care
        return TradePoint(false)
    }

    fun computePrice(openPrice: Double, tradeDirection: TradeDirection): Double {
        return when (tradeDirection) {
            TradeDirection.SHORT -> computeShortPrice(openPrice)
            TradeDirection.LONG -> computeLongPrice(openPrice)
        }
    }

    private fun computeShortPrice(openPrice: Double): Double {
        return openPrice * multiplier
    }

    private fun computeLongPrice(openPrice: Double): Double {
        return openPrice * (2.0 - multiplier)
    }

    fun computeTradeDirection(
        open: Double,
        high: Double,
        low: Double
    ): TradeDirection? {
        return if(open * multiplier < high) {
            TradeDirection.SHORT
        } else if(open * (2.0 - multiplier) > low) {
            TradeDirection.LONG
        } else {
            null
        }
    }
}

data class TradePoint(
    val isSuccess: Boolean,
    val point: MarketDataRecord? = null,
)