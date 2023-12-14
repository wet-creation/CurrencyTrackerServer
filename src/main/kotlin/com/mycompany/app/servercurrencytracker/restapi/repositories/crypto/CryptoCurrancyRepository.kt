package com.mycompany.app.servercurrencytracker.restapi.repositories.crypto

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CryptoCurrancyRepository : JpaRepository<Crypto, String> {
    @Query("SELECT c FROM Crypto c WHERE c.symbol = :symbol AND c.last_updated = (SELECT MAX(c2.last_updated) FROM Crypto c2 WHERE c2.symbol = :symbol)")
    fun findLatestBySymbol(symbol: String): Crypto?

    @Query("SELECT c FROM Crypto c WHERE c.last_updated = (SELECT MAX(last_updated) FROM Crypto WHERE symbol = c.symbol) ORDER BY (SELECT MAX(c1.market_cap_rank) FROM Crypto c1 WHERE c1.symbol = c.symbol)")
    fun findLatest(): List<Crypto>?

    @Query(value = "SELECT * FROM crypto WHERE last_updated >= ?1 AND last_updated < ?1 + 86400 and (?2 IS NULL OR symbol = ?2)", nativeQuery = true)
    fun findCryptoByDate(timestamp: Long, symbol: String? = null) : List<Crypto>?
    @Query(value = "SELECT * FROM crypto WHERE last_updated >=?1 and last_updated <?2 AND (?3 IS NULL OR symbol = ?3)", nativeQuery = true)
    fun findCryptoByRangeDate(timestampStart: Long, timestampEnd: Long, symbol: String? = null): List<Crypto>?

}