package com.mycompany.app.servercurrencytracker.restapi.repositories.currancy

import com.mycompany.app.servercurrencytracker.restapi.models.currancy.Rate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RatesRepository: JpaRepository<Rate, Long> {
    @Query(value = "SELECT rate FROM rate WHERE timestamp > ?1 - 1800 AND timestamp < ?1 + 1800 AND symbol = ?2", nativeQuery = true)
    fun findRateByDateAndSymbol(timestamp: Long, symbol: String): Double?
}