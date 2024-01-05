package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.CryptoFiatRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CryptoFiatRepository : JpaRepository<CryptoFiatRate, String> {

    @Query(value = "SELECT * FROM crypto_fiat_rate_view WHERE symbol COLLATE utf8mb4_bin = ?", nativeQuery = true)
    fun findLatest(symbol: String): CryptoFiatRate?
}