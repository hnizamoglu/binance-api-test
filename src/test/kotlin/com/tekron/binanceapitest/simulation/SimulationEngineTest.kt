package com.tekron.binanceapitest.simulation

import com.tekron.binanceapitest.adapter.CoinSymbol
import com.tekron.binanceapitest.model.MarketDataRecord
import com.tekron.binanceapitest.parser.BinanceDataParser
import com.tekron.binanceapitest.strategy.TradeDirection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.stream.Stream

class SimulationEngineTest {
    private val logger = KotlinLogging.logger {  }

    @Test
    fun `should simulate trades`(){
        val allBuyPoints = mutableListOf<BuyPoint>()
        CoinSymbol.entries.forEach{
            println("------------")
            logger.info { it.name }
            val sellRecords = BinanceDataParser().parse(File("historical-data/${it.name}/data-5m.json")).sortedBy { it.openTime }
            val buyRecords = BinanceDataParser().parse(File("${it.name}.json")).sortedBy { it.openTime }
            val engine = SimulationEngine(
                buyRecords = buyRecords,
                sellRecords = sellRecords,
                multiplier = 1.1,
            )
            val buyPoints = engine.work()
            allBuyPoints.addAll(buyPoints)
            printSummary(buyPoints)
            println("------------")

        }
        printSummary(allBuyPoints)
    }

    @ParameterizedTest
    @MethodSource("provideDataForTradeDirection")
    fun `should compute trade direction`(open: Double, high: Double, low: Double, expected: TradeDirection) {
        // given
        val engine = SimulationEngine(listOf(), listOf(), multiplier = 1.1)
        // when
        val direction = engine.computeTradeDirection(open, high, low)
        // then
        Assertions.assertThat(direction).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("provideDataForComputePrice")
    fun `should compute price`(openPrice: Double, tradeDirection: TradeDirection, expected: Double) {
        // given
        val engine = SimulationEngine(listOf(), listOf(), multiplier = 1.1)
        // when
        val price = engine.computePrice(openPrice, tradeDirection)
        // then
        Assertions.assertThat(BigDecimal(price).setScale(5, RoundingMode.HALF_EVEN).toDouble()).isEqualTo(expected)
    }

    @Test
    fun `should success with long trade`() {
        // given
        val now = LocalDateTime.now()
        val engine = SimulationEngine(
            buyRecords = listOf(),
            sellRecords = listOf(
                mockk {
                    every { openTime } returns now.minusHours(10)
                },
                mockk {
                    every { openTime } returns now.minusHours(8)
                },
                mockk {
                    every { openTime } returns now.minusHours(2)
                    every { high } returns 103.0
                    every { low } returns 98.0
                },
                mockk {
                    every { openTime } returns now.minusHours(1)
                    every { high } returns 111.0
                    every { low } returns 96.0
                }
            ),
            multiplier = 1.1
        )
        val marketDataRecord = mockk<MarketDataRecord> {
            every { openTime } returns now.minusHours(3)
        }
        // when
        val tradePoint = engine.checkSuccess(price = 100.0, marketDataRecord = marketDataRecord, tradeDirection = TradeDirection.LONG)
        // then
        Assertions.assertThat(tradePoint.isSuccess).isTrue()
        with(tradePoint.point) {
            Assertions.assertThat(this?.high).isEqualTo(111.0)
            Assertions.assertThat(this?.low).isEqualTo(96.0)
        }
    }

    @Test
    fun `should fail with long trade`() {
        // given
        val now = LocalDateTime.now()
        val engine = SimulationEngine(
            buyRecords = listOf(),
            sellRecords = listOf(
                mockk {
                    every { openTime } returns now.minusHours(10)
                },
                mockk {
                    every { openTime } returns now.minusHours(8)
                },
                mockk {
                    every { openTime } returns now.minusHours(2)
                    every { high } returns 103.0
                    every { low } returns 88.0
                },
                mockk {
                    every { openTime } returns now.minusHours(1)
                    every { high } returns 111.0
                    every { low } returns 96.0
                }
            ),
            multiplier = 1.1
        )
        val marketDataRecord = mockk<MarketDataRecord> {
            every { openTime } returns now.minusHours(3)
        }
        // when
        val tradePoint = engine.checkSuccess(price = 100.0, marketDataRecord = marketDataRecord, tradeDirection = TradeDirection.LONG)
        // then
        Assertions.assertThat(tradePoint.isSuccess).isFalse()
        with(tradePoint.point) {
            Assertions.assertThat(this?.high).isEqualTo(103.0)
            Assertions.assertThat(this?.low).isEqualTo(88.0)
        }
    }

    @Test
    fun `should success with short trade`() {
        // given
        val now = LocalDateTime.now()
        val engine = SimulationEngine(
            buyRecords = listOf(),
            sellRecords = listOf(
                mockk {
                    every { openTime } returns now.minusHours(10)
                },
                mockk {
                    every { openTime } returns now.minusHours(8)
                },
                mockk {
                    every { openTime } returns now.minusHours(2)
                    every { high } returns 103.0
                    every { low } returns 88.0
                },
                mockk {
                    every { openTime } returns now.minusHours(1)
                    every { high } returns 111.0
                    every { low } returns 96.0
                }
            ),
            multiplier = 1.1
        )
        val marketDataRecord = mockk<MarketDataRecord> {
            every { openTime } returns now.minusHours(3)
        }
        // when
        val tradePoint = engine.checkSuccess(price = 100.0, marketDataRecord = marketDataRecord, tradeDirection = TradeDirection.SHORT)
        // then
        Assertions.assertThat(tradePoint.isSuccess).isTrue()
        with(tradePoint.point) {
            Assertions.assertThat(this?.high).isEqualTo(103.0)
            Assertions.assertThat(this?.low).isEqualTo(88.0)
        }
    }

    @Test
    fun `should fail with short trade`() {
        // given
        val now = LocalDateTime.now()
        val engine = SimulationEngine(
            buyRecords = listOf(),
            sellRecords = listOf(
                mockk {
                    every { openTime } returns now.minusHours(10)
                },
                mockk {
                    every { openTime } returns now.minusHours(8)
                },
                mockk {
                    every { openTime } returns now.minusHours(2)
                    every { high } returns 111.0
                    every { low } returns 92.0
                },
                mockk {
                    every { openTime } returns now.minusHours(1)
                    every { high } returns 102.0
                    every { low } returns 70.0
                }
            ),
            multiplier = 1.1
        )
        val marketDataRecord = mockk<MarketDataRecord> {
            every { openTime } returns now.minusHours(3)
        }
        // when
        val tradePoint = engine.checkSuccess(price = 100.0, marketDataRecord = marketDataRecord, tradeDirection = TradeDirection.SHORT)
        // then
        Assertions.assertThat(tradePoint.isSuccess).isFalse()
        with(tradePoint.point) {
            Assertions.assertThat(this?.high).isEqualTo(111.0)
            Assertions.assertThat(this?.low).isEqualTo(92.0)
        }
    }

    fun printSummary(points: List<BuyPoint>){
        var match = 0
        var fail = 0
        logger.info { "total buy points: ${points.size}" }
        points.forEach {
            if (it.isSuccess!!) {
                match++
            } else {
                fail++
            }
            logger.info {
                "dir: ${it.tradeDirection} " +
                        "success: ${it.isSuccess} " +
                        "buyPrice: ${it.buyPrice.toString(3)}, " +
                        "targetPrice:${it.targetPrice.toString(3)}, " +
                        "actualHigh: ${it.sellRecord?.high?.toString(3)} " +
                        "actualLow: ${it.sellRecord?.low?.toString(3)}"
            }
        }
        logger.info { "match: $match , fail: $fail" }
    }

    companion object {
        @JvmStatic
        fun provideDataForTradeDirection(): Stream<Arguments> {
            return Stream.of(
                // open, high, low, expected (with 1.1 setting)
                Arguments.of(10,15,9.5,TradeDirection.SHORT),
                Arguments.of(100,105, 89, TradeDirection.LONG ),
                Arguments.of(100, 112, 88, TradeDirection.SHORT ),
            )
        }

        @JvmStatic
        fun provideDataForComputePrice(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(100, TradeDirection.LONG, 90 ),
                Arguments.of(100, TradeDirection.SHORT, 110 ),
            )
        }
    }
}

fun Double.toString(precision: Int): String = "%.${precision}f".format(this)