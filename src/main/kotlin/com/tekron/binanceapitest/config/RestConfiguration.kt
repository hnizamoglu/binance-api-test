package com.tekron.binanceapitest.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    fun jsonMapper(): JsonMapper {
        return JsonMapper()
    }

    @Bean
    fun httpClient(): OkHttpClient {
        return OkHttpClient()
    }
}