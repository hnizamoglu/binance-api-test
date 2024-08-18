package com.tekron.binanceapitest

import com.fasterxml.jackson.databind.ObjectMapper
import com.tekron.binanceapitest.adapter.BinanceWebSocketClient
import com.tekron.binanceapitest.adapter.KlineResponse
import org.junit.jupiter.api.Test

class BinanceWebSocketClientTest {

    @Test
    fun `should open web socket`() {

        val wsClient = BinanceWebSocketClient()
        while (true) {
            Thread.sleep(2000)
        }
    }

    @Test
    fun `should deserialize kline response`() {
        val content = """
            {"e":"kline","E":1723730148776,"s":"ARBUSDC","k":{"t":1723723200000,"T":1723737599999,"s":"ARBUSDC","i":"4h","f":1597997,"L":1599204,"o":"0.56460000","c":"0.56540000","h":"0.57040000","l":"0.56150000","v":"1285490.90000000","n":1208,"x":false,"q":"727433.83825000","V":"640138.40000000","Q":"362115.85068000","B":"0"}}
        """.trimIndent()
        val mapper = ObjectMapper()
        val result = mapper.readValue(content, KlineResponse::class.java)
        println(result)
    }
}