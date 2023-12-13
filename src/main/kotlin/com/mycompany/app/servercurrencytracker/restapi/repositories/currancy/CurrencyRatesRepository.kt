package com.mycompany.app.servercurrencytracker.restapi.repositories.currancy

import com.mycompany.app.servercurrencytracker.restapi.models.currancy.CurrencyRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CurrencyRatesRepository: JpaRepository<CurrencyRate, Long> {
    @Query(value = "SELECT * FROM currency_rate_view WHERE timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findLatestRate(): List<CurrencyRate>?
    @Query(value = "SELECT * FROM currency_rate_view WHERE symbol =? and timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findRateBySymbol(symbol: String): CurrencyRate?

    @Query(value = "SELECT * FROM currency_rate_view WHERE timestamp >= ?1 AND timestamp < ?1 + 86400 AND (?2 IS NULL OR symbol = ?2)", nativeQuery = true)
    fun findRateByDate(timestamp: Long, symbol: String? = null): List<CurrencyRate>?

    @Query(value = "SELECT * FROM currency_rate_view WHERE timestamp >=?1 and timestamp <?2 AND (?3 IS NULL OR symbol = ?3)", nativeQuery = true)
    fun findRateByRangeDate(timestampStart: Long, timestampEnd: Long, symbol: String? = null): List<CurrencyRate>?
}