package com.tekron.binanceapitest.adapter

import com.binance.connector.client.impl.WebSocketStreamClientImpl
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class BinanceWebSocketClient {
    private val BASE_URL = "wss://stream.binance.com:443/ws/arbusdc@kline_4h"
    private val httpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    init {
        val request = Request.Builder().url(BASE_URL).build()
        webSocket = httpClient.newWebSocket(request, BinanceWsListener())
        httpClient.dispatcher.executorService.shutdown()
    }

}

//@Component
class BinanceWebSocketClientWithBinanceLib(
    private val mapper: ObjectMapper = ObjectMapper()
) {
    private val client = WebSocketStreamClientImpl()
    private val streams = arrayListOf("arbusdc@kline_4h")
    init {
        client.klineStream("arbusdc","4h"){
            println(it)
            kotlin.runCatching {
                mapper.readValue(it, KlineResponse::class.java)?.let {
                    val messageTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.messageTimestamp), ZoneOffset.UTC)
                    println("message time: $messageTime")
                    println("current time: ${LocalDateTime.now()}")
                }
            }
        }

    }
}

class BinanceWsListener(
    private val mapper: ObjectMapper = ObjectMapper()
): WebSocketListener() {
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        println("onClosed $reason")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        println("onClosing $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        println("onFailure $t")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        println("onMessage $text")
        kotlin.runCatching {
            mapper.readValue(text, KlineResponse::class.java)?.let {
                val messageTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.messageTimestamp), ZoneOffset.UTC)
                println("message time: $messageTime")
                println("current time: ${LocalDateTime.now()}")
            }
        }
    }

//    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//        super.onMessage(webSocket, bytes)
//        println("onMessage $bytes")
//    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        println("onOpen")
        val content = """
            {
              "method": "SUBSCRIBE",
              "params": [
                "arbusdc@kline_4h"
              ],
              "id": "${UUID.randomUUID()}"
            }
        """.trimIndent()
        println("sending sub message\n$content")
        val success = webSocket.send(content)
        when(success) {
            true -> println("subscribed")
            false -> println("subscribe failed")
        }
    }
}

class KlineResponse(
    @JsonProperty("e")
    val messageType: String,
    @JsonProperty("E")
    val messageTimestamp: Long,
    @JsonProperty("s")
    val symbol: String,
    @JsonProperty("k")
    val detail: KlineDetail,
)

class KlineDetail(
    @JsonProperty("t")
    val startTime: Long,
    @JsonProperty("T")
    val endTime: Long,
    @JsonProperty("s")
    val symbol: String,
    @JsonProperty("i")
    val interval: String,
    @JsonProperty("f")
    val firstTradeId: Long,
    @JsonProperty("L")
    val lastTradeId: Long,
    @JsonProperty("o")
    val openPrice: Double,
    @JsonProperty("c")
    val closePrice: Double,
    @JsonProperty("h")
    val highPrice: Double,
    @JsonProperty("l")
    val lowPrice: Double,
    @JsonProperty("v")
    val baseAssetVolume: Double,
    @JsonProperty("n")
    val numberOfTrades: Int,
    @JsonProperty("x")
    val isClosed: Boolean,
    @JsonProperty("q")
    val quoteAssetVolume: Double,
    @JsonProperty("V")
    val takerBuyAssetVolume: Double,
    @JsonProperty("Q")
    val takerBuyQuoteVolume: Double,
    @JsonProperty("B")
    val ignore: Int
)
