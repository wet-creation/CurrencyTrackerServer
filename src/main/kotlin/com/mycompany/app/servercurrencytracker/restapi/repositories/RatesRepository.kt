package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.Rate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RatesRepository: JpaRepository<Rate, Long> {
    @Query(value = "SELECT * FROM rate WHERE symbol =? and timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findRateBySymbol(symbol: String): Rate?

    @Query(value = "SELECT * FROM rate WHERE timestamp > ?1 AND timestamp < ?1 + 86400000 AND (?2 IS NULL OR symbol = ?2)", nativeQuery = true)
    fun findRateByDate(timestamp: Long, symbol: String? = null): List<Rate>?

    @Query(value = "SELECT * FROM rate WHERE timestamp >?1 and timestamp <?2 AND (?3 IS NULL OR symbol = ?3)", nativeQuery = true)
    fun findRateByRangeDate(timestampStart: Long, timestampEnd: Long, symbol: String? = null): List<Rate>?

    @Query(value = "SELECT * FROM rate WHERE timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findLatestRate(): List<Rate>?
}