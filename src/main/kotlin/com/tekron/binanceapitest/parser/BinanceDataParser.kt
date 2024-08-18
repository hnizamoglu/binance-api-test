package com.tekron.binanceapitest.parser

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.tekron.binanceapitest.model.MarketDataRecord
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class BinanceDataParser {
    fun parse(file: File): List<MarketDataRecord> {
        val mapper = JsonMapper()
        val jsonNode = mapper.readTree(file)
        return map(jsonNode)
    }

    fun parse(content: String): List<MarketDataRecord> {
        val mapper = JsonMapper()
        val jsonNode = mapper.readTree(content)
        return map(jsonNode)
    }
    private fun map(jsonNode: JsonNode): List<MarketDataRecord> {
        return jsonNode.map { tokens->
            MarketDataRecord(
                openTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokens[0].asLong()), ZoneId.of("UTC")),
                open = tokens[1].asDouble(),
                high = tokens[2].asDouble(),
                low = tokens[3].asDouble(),
                close = tokens[4].asDouble(),
                volume = tokens[5].asDouble(),
                closeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(tokens[6].asLong()), ZoneId.of("UTC")),
                quoteVolume = tokens[7].asDouble(),
                count = tokens[8].asInt(),
                takerBuyVolume = tokens[9].asDouble(),
                takerBuyQuoteVolume = tokens[10].asDouble(),
                ignore = tokens[11].asBoolean()
            )
        }
    }
}

