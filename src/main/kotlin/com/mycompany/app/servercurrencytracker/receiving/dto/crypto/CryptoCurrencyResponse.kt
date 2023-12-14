package com.mycompany.app.servercurrencytracker.receiving.dto.crypto

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class CryptoCurrencyResponse(
    val ath: Double,
    val ath_change_percentage: Double,
    val ath_date: String,
    val atl: Double,
    val atl_change_percentage: Double,
    val atl_date: String,
    val circulating_supply: Double,
    val current_price: Double,
    val fully_diluted_valuation: Long,
    val high_24h: Double,
    val id: String,
    val image: String,
    val last_updated: String,
    val low_24h: Double,
    val market_cap: Long,
    val market_cap_change_24h: Double,
    val market_cap_change_percentage_24h: Double,
    val market_cap_rank: Long,
    val max_supply: Any,
    val name: String,
    val price_change_24h: Double,
    val price_change_percentage_24h: Double,
    val roi: Any,
    val symbol: String,
    val total_supply: Double,
    val total_volume: Long
) {
    fun toCryptoDetails() :Crypto{
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val parsed = LocalDateTime.parse(last_updated, dateFormat)
        val timestamp = parsed.toEpochSecond(ZoneOffset.UTC)
        return Crypto(name,symbol,current_price,image,timestamp,market_cap_rank)
    }

}