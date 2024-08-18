package com.tekron.binanceapitest.strategy

import com.tekron.binanceapitest.model.MarketDataRecord
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ParameterizedTradingStrategy: TradingStrategy {
    private val multiplier = 1.1
    private val profitThreshold = 0.10
    private val lossThreshold = 0.10

    override fun execute(data: MarketDataRecord): TradingStrategyResult? {
        return computeTradeDirection(data)?.let{
            TradingStrategyResult(
                detectedAt = LocalDateTime.now(),
                indexPrice = data.close,
                tradeDirection = it,
                targetPrice = data.close * (1 + profitThreshold),
                openTime = data.openTime,
                closeTime = data.closeTime,
                openPrice = data.open,
                highPrice = data.high,
                lowPrice = data.low,
                closePrice = data.close
            )
        }
    }

    fun computeTradeDirection(data: MarketDataRecord): TradeDirection? {
        // data.close indicates current price. if it's working on historical data
        // data.close needs to be -> data.low and data.high in order
        return if (data.open > data.close * multiplier) {
            TradeDirection.LONG
        } else if(data.open * multiplier < data.close) {
            TradeDirection.SHORT
        } else {
            null
        }
    }
}