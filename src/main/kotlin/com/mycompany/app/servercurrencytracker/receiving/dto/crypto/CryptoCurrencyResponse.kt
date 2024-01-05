package com.mycompany.app.servercurrencytracker.receiving.dto.crypto

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class CryptoCurrencyResponse(
    val ath: Double,//собирать
    val ath_change_percentage: Double,
    val ath_date: String,//собирать
    val atl: Double,//собирать
    val atl_change_percentage: Double,
    val atl_date: String,//собирать
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
    val max_supply: Double?,
    val name: String,
    val price_change_24h: Double,
    val price_change_percentage_24h: Double,
    val roi: Any,
    val symbol: String,
    val total_supply: Double?,
    val total_volume: Long,
) {
    fun toCryptoDetails(): Crypto {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val parsedLastUpdated = LocalDateTime.parse(last_updated, dateFormat)
        val timestamp = parsedLastUpdated.toEpochSecond(ZoneOffset.UTC)
        val parsedAth = LocalDateTime.parse(ath_date, dateFormat)
        val timestampAth = parsedAth.toEpochSecond(ZoneOffset.UTC)
        val parsedAtl = LocalDateTime.parse(atl_date, dateFormat)
        val timestampAtl = parsedAtl.toEpochSecond(ZoneOffset.UTC)
        return Crypto(
            name,
            symbol,
            current_price,
            image,
            timestamp,
            market_cap_rank,
            market_cap,
            max_supply,
            total_supply,
            circulating_supply,
            ath,
            timestampAth,
            atl,
            timestampAtl,
            current_price-price_change_24h,
            null,
            null
        )
    }

}