package com.tekron.binanceapitest.telegram

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class PublicChannelTest {
    private val botToken = "6980471202:AAGorrDRmKp285vBvIjXAUps2rL0ntypIcE"
    private val berlinEvKanaliChannelId = "-1001832409794"
    private lateinit var botApp: TelegramBotsLongPollingApplication
    private lateinit var tgClient: OkHttpTelegramClient

    @BeforeEach
    fun setup() {
        botApp = TelegramBotsLongPollingApplication()

        tgClient = OkHttpTelegramClient(botToken)
    }
    @Test
    fun `should receive message`() {
        botApp.registerBot(botToken,TelegramPublicChannelBot(tgClient))
        while(true){
            Thread.sleep(2000)
        }
    }

    @Test
    fun `should send message`() {
        // not all markdown features work with telegram message api (unfortunately)
        val regex = Regex("_|[*`~>#+=|{}()\\\\.!-]")
        val messageText = """
            > Some quote

            [Link to Google](https://www.google.com)

            *Bold Text*

            ```    
            | Syntax | Description |
            | ----------- | ----------- |
            | Header | Title |
            | Paragraph | Text |
            ```

        """.trimIndent()
        println(messageText)

        val msg = SendMessage(berlinEvKanaliChannelId, messageText)
        msg.enableMarkdownV2(true)

        tgClient.execute(msg)

    }

    @Test
    fun `should send proper alert message`() {
        val content = """
            ```
            Date    : 15:13:23 1/7/2024 (UTC)
            Symbol  : ARBUSDC
            Trade   : LONG
            Price   : 0.47
            Target  : 0.493
            ```
        """.trimIndent()
        val msg = SendMessage(berlinEvKanaliChannelId, content)
        msg.enableMarkdownV2(true)

        tgClient.execute(msg)
    }

}

class TelegramPublicChannelBot(
    val client: TelegramClient
): LongPollingSingleThreadUpdateConsumer {
    override fun consume(update: Update?) {
        update?.message?.let {
            println(it.text)
            val message = SendMessage(
                it.chat.id.toString(),it.text
            )
            client.execute(message)
        }
    }
}