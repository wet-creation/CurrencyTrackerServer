package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.Rate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RatesRepository: JpaRepository<Rate, Long> {
    @Query(value = "SELECT * FROM rate WHERE symbol =? and timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findRateBySymbol(symbol: String): List<Rate>?

    @Query(value = "SELECT * FROM rate WHERE timestamp >?1 and timestamp <?1 + 86400000", nativeQuery = true)
    fun findRateByDate(timestamp: Long): List<Rate>?

    @Query(value = "SELECT * FROM rate WHERE timestamp >?1 and timestamp <?2", nativeQuery = true)
    fun findRateByRangeDate(timestampStart: Long, timestampEnd: Long): List<Rate>?

    @Query(value = "SELECT * FROM rate WHERE timestamp = (select max(timestamp) from rate)", nativeQuery = true)
    fun findLatestRate(): List<Rate>?
}