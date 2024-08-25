package com.tekron.binanceapitest.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.tekron.binanceapitest.notification.GenericNotificationMessage
import com.tekron.binanceapitest.notification.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetAddress

class TelegramAppender: AppenderBase<ILoggingEvent>() {
    var notificationService: NotificationService? = null

    override fun append(event: ILoggingEvent?) {
        notificationService?.let {
            event?.message?.let{
                notificationService!!.notify(GenericNotificationMessage("""
                ```
                ${InetAddress.getLocalHost().hostName}
                ${event.message}
                ```
            """.trimIndent()))
            }
        }
    }
}

@Component
class TelegramLogConfiguration(
    private val notificationService: NotificationService
) {
    init {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)
        rootLogger.iteratorForAppenders().forEach {
            if(it is TelegramAppender) {
                it.notificationService = notificationService
            }
        }
    }
}