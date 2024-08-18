package com.tekron.binanceapitest.notification

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient

@Configuration
class TelegramConfiguration {
    private val botToken = "6980471202:AAGorrDRmKp285vBvIjXAUps2rL0ntypIcE"

    @Bean
    fun telegramBotClient() =
        OkHttpTelegramClient(botToken)
}