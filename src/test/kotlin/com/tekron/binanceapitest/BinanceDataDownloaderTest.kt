package com.tekron.binanceapitest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.tekron.binanceapitest.adapter.BinanceDataDownloader
import com.tekron.binanceapitest.adapter.CoinSymbol
import io.github.oshai.kotlinlogging.KotlinLogging
import io.mockk.mockk
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test

class BinanceDataDownloaderTest {
    private val logger = KotlinLogging.logger {}
    @Test
    fun `should download data`() {

        CoinSymbol.entries.forEach {
            val downloader = BinanceDataDownloader(
                OkHttpClient(),
                JsonMapper(),
                mockk(relaxed = true)
            )
            downloader.downloadData(it)
        }

    }

    @Test
    fun `should build http url`() {
        val downloader = BinanceDataDownloader(
            OkHttpClient(),
            JsonMapper(),
            mockk(relaxed = true)
        )
        val url = downloader.buildHttpUrl(CoinSymbol.ARBUSDC)
        println(url.toString())
    }

    @Test
    fun `should fetch recent data`() {
        val mapper = ObjectMapper()
        val downloader =
            BinanceDataDownloader(
                OkHttpClient(),
                JsonMapper(),
                mockk(relaxed = true)
            )
        val resp = downloader.getRecentKline(CoinSymbol.ARBUSDC)
        mapper.readTree(resp)[0]
    }
    @Test
    fun `log test`(){
        logger.info { "test" }
    }
}