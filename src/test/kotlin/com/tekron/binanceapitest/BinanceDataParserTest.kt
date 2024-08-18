package com.tekron.binanceapitest

import com.tekron.binanceapitest.parser.BinanceDataParser
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class BinanceDataParserTest {

    @Test
    fun `should parse binance data json`() {
        val filepath = "ARBUSDC.json"
        val parser = BinanceDataParser()
        parser.parse(File(filepath))
    }

    @Test
    fun `dummy`(){
        val dur = Duration.parse("PT5M")
        println(dur.seconds)
//        println(Instant.now().minus(dur.toLong(DurationUnit.MINUTES), ChronoUnit.MINUTES).toEpochMilli())
        println(Instant.now().minus(dur))
        println(dur.multipliedBy(2000))
    }
}