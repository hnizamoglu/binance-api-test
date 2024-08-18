package com.tekron.binanceapitest.notification

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class TelegramNotificationServiceTest {

    private lateinit var service: TelegramNotificationService
    private val tgClient = mockk<OkHttpTelegramClient>()
    @BeforeEach
    fun setup() {
        service = TelegramNotificationService(tgClient)
    }

    @Test
    fun `should send trade notification`() {
        // given
        val message = TradeNotificationMessage(
            date = "00:00:00 1/1/2000",
            symbol = "ARBUSDC",
            direction = "LONG",
            currentPrice = "0.10",
            targetPrice = "0.12"
        )
        every { tgClient.execute(any<SendMessage>()) } returns mockk()

        // when
        service.notify(message)

        // then
        verify { tgClient.execute(any<SendMessage>()) }
    }
}