package com.tekron.binanceapitest.strategy

import com.tekron.binanceapitest.model.MarketDataRecord
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParameterizedTradingStrategyTest {

    private lateinit var strategy: TradingStrategy
    @BeforeEach
    fun setup() {
        strategy = ParameterizedTradingStrategy()
    }
    @Test
    fun `should find a result for long`() {
        // given
        val marketDataRecord = mockk<MarketDataRecord>(relaxed = true) {
            every { open } returns 100.0
            every { close } returns 89.0
        }
        // when
        val result = strategy.execute(marketDataRecord)
        //then
        Assertions.assertThat(result).isNotNull
        with(result!!){
            Assertions.assertThat(tradeDirection).isEqualTo(TradeDirection.LONG)
            Assertions.assertThat(indexPrice).isEqualTo(89.0)
            Assertions.assertThat(openPrice).isEqualTo(100.0)
        }
    }
    @Test
    fun `should find a result for short`() {
        // given
        val marketDataRecord = mockk<MarketDataRecord>(relaxed = true) {
            every { open } returns 100.0
            every { close } returns 110.1
        }
        // when
        val result = strategy.execute(marketDataRecord)
        //then
        Assertions.assertThat(result).isNotNull
        with(result!!){
            Assertions.assertThat(tradeDirection).isEqualTo(TradeDirection.SHORT)
            Assertions.assertThat(indexPrice).isEqualTo(110.1)
            Assertions.assertThat(openPrice).isEqualTo(100.0)
        }
    }
    @Test
    fun `should find no result`() {
        // given
        val marketDataRecord = mockk<MarketDataRecord>(relaxed = true) {
            every { open } returns 100.0
            every { close } returns 102.0
        }
        // when
        val result = strategy.execute(marketDataRecord)
        //then
        Assertions.assertThat(result).isNull()
    }
}