package com.tekron.binanceapitest.notification

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sentry.Sentry
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class TelegramNotificationService(
    private val tgClient: OkHttpTelegramClient
): NotificationService {
    private val logger = KotlinLogging.logger {  }
    private val defaultChannel = "-1001832409794"

    override fun notify(message: NotificationMessage) {
        val msg = SendMessage(defaultChannel, message.toMessage())
        msg.enableMarkdownV2(true)
        tgClient.execute(msg)
        logger.info { "telegram notification sent..." }
    }

    @Scheduled(cron = "0 0 */4 * * *")
    fun heartbeat() {
        logger.info { "sending heartbeat" }
        tgClient.execute(SendMessage(defaultChannel, "Heartbeat"))
    }

    @Scheduled(cron = "0 0 */1 * * *")
    fun sentryHeartbeat() {
        Sentry.captureMessage("Heartbeat")
    }
}