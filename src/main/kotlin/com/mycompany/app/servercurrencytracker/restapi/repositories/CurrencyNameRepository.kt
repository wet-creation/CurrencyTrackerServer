package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.CurrencyName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CurrencyNameRepository: JpaRepository<CurrencyName, String> {
    @Query(value = "SELECT * FROM currency_name WHERE (?1 IS NULL OR symbol = ?1)", nativeQuery = true)
    fun findCurrencyBySymbol(symbol: String? = null): List<CurrencyName>?
}