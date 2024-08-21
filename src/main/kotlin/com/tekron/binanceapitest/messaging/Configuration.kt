package com.tekron.binanceapitest.messaging

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class Configuration{

//    @Bean
//    @Profile("receiver")
//    fun receiver(): Receiver{
//        return Receiver()
//    }

    @Bean
//    @Profile("sender")
    fun sender(
        rabbitTemplate: RabbitTemplate,
        queue: Queue,
    ): Sender {
        return Sender(rabbitTemplate, queue)
    }

    @Bean
    fun queue(): Queue {
        return Queue(SYMBOL_FETCH_QUEUE_NAME)
    }

    companion object {
        const val SYMBOL_FETCH_QUEUE_NAME = "symbol-fetch-queue"
    }
}