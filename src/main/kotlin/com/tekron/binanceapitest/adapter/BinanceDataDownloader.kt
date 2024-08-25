package com.tekron.binanceapitest.adapter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.tekron.binanceapitest.notification.GenericNotificationMessage
import com.tekron.binanceapitest.notification.NotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

@Component
class BinanceDataDownloader(
    private val httpClient: OkHttpClient,
    private val jsonMapper: JsonMapper,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger{}
    val BASE_HOST = "data-api.binance.vision"
    val SCHEME = "https"
    val KLINES_URL = "api/v3/klines"
    val INTERVAL = "5m"

    fun downloadData(
        symbol: CoinSymbol,
        earliest: Long = LocalDateTime.now().minusMonths(9).toInstant(ZoneOffset.UTC).toEpochMilli()
    ) {
        createDirectoryIfNotExists("historical-data")
        val result = jsonMapper.createArrayNode()
        val now = LocalDateTime.now().withMinute(0).withHour(0).withSecond(0).toInstant(ZoneOffset.UTC)
        val maxLimit = 1000L
        val intervalDuration = INTERVAL.toDuration().multipliedBy(maxLimit)
        val intervalDurationMs = intervalDuration.toMillis()
        var repeat = (270.0 / intervalDuration.toDays()).roundToInt()
        var endTime: Long = now.toEpochMilli()
        var startTime: Long = endTime - intervalDurationMs
        logger.info { "start: $startTime end: $endTime earliest: $earliest" }

        while(repeat-- > 0) {
            logger.info { "downloading for endTime: $endTime" }
            val node = getJsonNode(symbol, start = startTime, end = endTime, maxLimit)
            result.addAll(node as ArrayNode)
            endTime = startTime
            startTime -= intervalDurationMs
            Thread.sleep(2000)
        }

        logger.info { "download complete.writing content to file..." }
        FileOutputStream(File("historical-data/${symbol.name}-${INTERVAL}.json")).use {
            it.write(result.toPrettyString().encodeToByteArray())
            it.flush()
        }
        logger.info { "data write complete." }
    }

    private fun createDirectoryIfNotExists(directoryName: String) {
        val path = Path.of(directoryName)
        if(!Files.isDirectory(path)) {
            Files.createDirectory(Path.of(directoryName))
        }
    }

    fun getRecentKline(
        symbol: CoinSymbol,
    ): String? {
        return getJsonString(symbol = symbol, limit = 1)
    }

    fun getJsonNode(symbol: CoinSymbol, start: Long? = null, end: Long? = null, limit: Long? = null): JsonNode {
        val str = getJsonString(
            symbol = symbol,
            start = start,
            end = end,
            limit = limit
        )
        return str?.let {
            return jsonMapper.readTree(it)
        } ?: jsonMapper.readTree("[]")
    }

    fun getJsonString(
        symbol: CoinSymbol,
        start: Long? = null,
        end: Long? = null,
        limit: Long? = null
    ): String? {
        val request = buildRequest(symbol, start, end, limit)
        val response = httpClient.newCall(request = request).execute()

        if(response.code == 429) {
            notificationService.notify(GenericNotificationMessage("Binance API Limit reached!"))
            logger.error { "Binance API blocked!" }
            logger.error { "Headers:" }
            response.headers.filter { it.first.startsWith("x-mbx-") }.forEach {
                logger.info { "${it.first}: ${it.second}" }
            }
            response.headers["retry-after"]?.let {
                logger.info { "retry-after: $it" }
            }
            exitProcess(0)
        }

        return response.body?.string()
    }

    fun buildRequest(
        symbol: CoinSymbol,
        start: Long? = null,
        end: Long? = null,
        limit: Long? = null
    ): Request {
        return Request.Builder()
            .get()
            .url(buildHttpUrl(symbol, start, end, limit))
            .build()
    }

    fun buildHttpUrl(
        symbol: CoinSymbol,
        start: Long? = null,
        end: Long? = null,
        limit: Long? = null
    ): HttpUrl {
        val builder = HttpUrl.Builder()
            .host(BASE_HOST)
            .scheme(SCHEME)
            .addPathSegments(KLINES_URL)
            .addQueryParameter("symbol", symbol.name)
            .addQueryParameter("interval",INTERVAL)
        start?.let { builder.addQueryParameter("startTime", start.toString()) }
        end?.let { builder.addQueryParameter("endTime", end.toString()) }
        limit?.let { builder.addQueryParameter("limit", limit.toString()) }
        return builder.build()
    }
}

enum class CoinSymbol(s: String) {
    ARBUSDC("ARBUSDC"),
    SOLUSDC("SOLUSDC"),
    DOGEUSDC("DOGEUSDC"),
    ETHUSDC("ETHUSDC"),
    BTCUSDC("BTCUSDC"),
    LINKUSDC("LINKUSDC"),
    UNIUSDC("UNIUSDC"),
    AVAXUSDC("AVAXUSDC"),
    BNBUSDC("BNBUSDC"),
    MATICUSDC("MATICUSDC"),
    OMNIUSDC("OMNIUSDC"),
    OPUSDC("OPUSDC"),
    XRPUSDC("XRPUSDC"),
}

fun Long.toInstant(): Instant {
    return Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toInstant()
}

fun String.toIsoDurationString() =
    "PT${this.uppercase()}"

fun String.toDuration() =
    Duration.parse(toIsoDurationString())