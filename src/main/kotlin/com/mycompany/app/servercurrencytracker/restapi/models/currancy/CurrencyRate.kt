package com.mycompany.app.servercurrencytracker.restapi.models.currancy

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Immutable

@Immutable
@Entity
data class CurrencyRate(
    @Id
    val id: Long,
    @Column
    val symbol: String,
    @Column
    val timestamp: Long,
    @Column
    val rate: Double,
    @Column
    var name: String
){
    constructor() : this(0,"",0,0.0,"")
}