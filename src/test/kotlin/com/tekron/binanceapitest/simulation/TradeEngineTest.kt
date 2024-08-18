package com.tekron.binanceapitest.simulation

import com.tekron.binanceapitest.adapter.CoinSymbol
import com.tekron.binanceapitest.parser.BinanceDataParser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.io.File

class TradeEngineTest {
    private val logger = KotlinLogging.logger {  }
    @Test
    fun `should work trade engine`() {
        val allBuyPoints = mutableListOf<BuyPoint>()
        CoinSymbol.entries.forEach{
            logger.info { it.name }
            val sellRecords = BinanceDataParser().parse(File("historical-data/${it.name}/data-5m.json")).sortedBy { it.openTime }
            val buyRecords = BinanceDataParser().parse(File("${it.name}.json")).sortedBy { it.openTime }
            val engine = TradeEngine(buyRecords, sellRecords)
            val buyPoints = engine.work()
            allBuyPoints.addAll(buyPoints)
        }
        var match = 0
        var fail = 0
        allBuyPoints.forEach {
            match += it.match
            fail += it.fail
        }
        logger.info { "match: $match , fail: $fail" }
        logger.info { allBuyPoints }
    }
}