package com.mycompany.app.servercurrencytracker.restapi.models.crypto

import jakarta.persistence.*

@Entity
data class Crypto(

    @Column
    val name: String,
    @Column
    val symbol: String,
    @Column
    val current_price: Double,
    @Column
    val image: String,
    @Column
    val last_updated: Long,
    @Column
    val market_cap_rank: Long,
    @Column
    val market_cap: Long,
    @Column(nullable = true)
    val max_supply: Double?,
    @Column(nullable = true)
    val total_supply: Double?,
    @Column
    val circulating_supply: Double,
    @Column
    val ath: Double,
    @Column
    val ath_timestamp: Long,
    val atl: Double,
    @Column
    val atl_timestamp: Long,
    @Column(nullable = true)
    val _24h: Double?,
    @Column(nullable = true)
    val _7d: Double?,
    @Column(nullable = true)
    val _1m: Double?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    constructor() : this("", "", 0.0, "", 0, 0, 0, null, null, 0.0, 0.0, 0, 0.0, 0, null, null, null)
}