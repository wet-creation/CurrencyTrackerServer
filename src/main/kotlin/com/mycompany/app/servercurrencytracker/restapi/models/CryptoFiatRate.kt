package com.mycompany.app.servercurrencytracker.restapi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Immutable

@Immutable
@Entity
data class CryptoFiatRate(
    @Id
    val symbol: String,
    @Column
    val timestamp: Long,
    @Column
    val rate: Double,
) {
    constructor() : this("", 0, 0.0)
}