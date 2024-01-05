package com.mycompany.app.servercurrencytracker.restapi.models.currancy

import com.mycompany.app.servercurrencytracker.receiving.dto.fiat.Latest
import jakarta.persistence.*


@Entity
data class Rate(
    @Column
    val symbol: String,
    @Column
    val timestamp: Long,
    @Column
    val rate: Double,
    @Column(nullable = true)
    val _24h: Double?,
    @Column(nullable = true)
    val _7d: Double?,
    @Column(nullable = true)
    val _1m: Double?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    constructor() : this("USD", 0, 1.0, null, null, null)
}

fun Latest.toRate(): List<Rate> {
    val ratesList = mutableListOf<Rate>()

    for ((symbol, rate) in rates) {
        val rate = Rate(
            timestamp = timestamp,
            symbol = symbol,
            rate = rate,
            _24h = null,
            _7d = null,
            _1m = null
        )
        ratesList.add(rate)
    }

    return ratesList
}


