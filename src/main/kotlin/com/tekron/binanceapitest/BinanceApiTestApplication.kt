package com.tekron.binanceapitest

import com.tekron.binanceapitest.adapter.CoinSymbol
import com.tekron.binanceapitest.model.MarketDataRecord
import com.tekron.binanceapitest.parser.BinanceDataParser
import com.tekron.binanceapitest.simulation.TradeEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sentry.Sentry
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.io.File
import java.io.FileNotFoundException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootApplication
@EnableScheduling
class BinanceApiTestApplication

val logger = KotlinLogging.logger {  }
fun main(args: Array<String>) {
    Sentry.init {options ->
        options.dsn = "https://5c34ee67a57b60be3dcf5f0ad5dbab5d@o4507826178228224.ingest.de.sentry.io/4507826179866704"
        // Set tracesSampleRate to 1.0 to capture 100% of transactions for tracing.
        // We recommend adjusting this value in production.
        options.tracesSampleRate = 1.0
        // When first trying Sentry it's good to see what the SDK is doing:
        options.isDebug = true
    }

    runApplication<BinanceApiTestApplication>(*args)
//    CoinSymbol.entries.forEach{
//        println(it.name)
//        val sellRecords = BinanceDataParser().parse(File("${it.name}.json")).sortedBy { it.openTime }
//        val buyRecords = BinanceDataParser().parse(File("${it.name}.json")).sortedBy { it.openTime }
//        val engine = TradeEngine(buyRecords, sellRecords)
//        engine.work()
//    }
}

class CsvParser {

    fun parseCsvRoot(directory: String): List<MarketDataRecord> {
        val rootDir = File(javaClass.getResource(directory)?.file ?: throw FileNotFoundException())
        return rootDir.listFiles()?.map {
            println("Processing ${it.name}")
            parseCsv(it)
        }?.flatten() ?: throw RuntimeException("error processing file $directory")

    }
    private fun parseCsv(file: File): List<MarketDataRecord> {
        val records = mutableListOf<MarketDataRecord>()
        file.forEachLine {
            val tokens = it.split(",")
            val record = MarketDataRecord(
                openTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokens[0].toLong()), ZoneId.of("UTC")),
                open = tokens[1].toDouble(),
                high = tokens[2].toDouble(),
                low = tokens[3].toDouble(),
                close = tokens[4].toDouble(),
                volume = tokens[5].toDouble(),
                closeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokens[6].toLong()), ZoneId.of("UTC")),
                quoteVolume = tokens[7].toDouble(),
                count = tokens[8].toInt(),
                takerBuyVolume = tokens[9].toDouble(),
                takerBuyQuoteVolume = tokens[10].toDouble(),
                ignore = tokens[11].toBoolean()
            )
            records.add(record)
        }
        return records
    }
    fun parseCsv(filePath: String): List<MarketDataRecord> {
        val file = File(javaClass.getResource(filePath)?.file ?: throw FileNotFoundException() )
        return parseCsv(file)
    }
}