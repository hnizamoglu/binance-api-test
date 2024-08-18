package com.tekron.binanceapitest.notification

interface NotificationService {
    fun notify(message: NotificationMessage)
}

class TradeNotificationMessage(
    val date: String,
    val symbol: String,
    val direction: String,
    val currentPrice: String,
    val targetPrice: String,
): NotificationMessage {
    override fun toMessage(): String {
        return """
            ```
            Date    : $date (UTC)
            Symbol  : $symbol
            Trade   : $direction
            Price   : $currentPrice
            Target  : $targetPrice
            ```
        """.trimIndent()
    }
}

class GenericNotificationMessage(
    val content: String,
): NotificationMessage {
    override fun toMessage(): String {
        return content
    }
}

interface NotificationMessage {
    fun toMessage(): String
}

