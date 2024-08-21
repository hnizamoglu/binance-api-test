package com.tekron.binanceapitest.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate

class Sender(
    private val rabbitTemplate: RabbitTemplate,
    private val queue: Queue
) {
    private val logger = KotlinLogging.logger {  }

    fun sendMessage(msg: String) {
        kotlin.runCatching {
            rabbitTemplate.convertAndSend(queue.name, msg)
        }.onFailure {
            logger.error { "error sending message to MQ" }
        }

    }


}