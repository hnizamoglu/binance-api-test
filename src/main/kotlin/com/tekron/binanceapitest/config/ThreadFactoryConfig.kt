package com.tekron.binanceapitest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.concurrent.ThreadPoolExecutor

@Configuration
class ThreadFactoryConfig {

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 10
        scheduler.setThreadNamePrefix("sched")
        scheduler.initialize()
        return scheduler
    }

    @Bean(name = ["threadPoolTaskExecutor"])
    fun threadPoolTaskExecutor(): ThreadPoolExecutor {
        return MyThreadPoolExecutor()
    }
}