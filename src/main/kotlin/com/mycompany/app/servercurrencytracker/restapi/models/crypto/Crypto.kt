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
    val market_cap_rank: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = ""
    constructor(): this("","",0.0,"",0,0)
}