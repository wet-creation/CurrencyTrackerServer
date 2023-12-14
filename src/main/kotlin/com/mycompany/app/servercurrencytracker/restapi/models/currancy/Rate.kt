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
    val rate: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    constructor() : this("USD", 0, 1.0)
}

fun Latest.toRate(): List<Rate> {
    val ratesList = mutableListOf<Rate>()

    for ((symbol, rate) in rates) {
        val rate = Rate(
            timestamp = timestamp,
            symbol = symbol,
            rate = rate
        )
        ratesList.add(rate)
    }

    return ratesList
}


