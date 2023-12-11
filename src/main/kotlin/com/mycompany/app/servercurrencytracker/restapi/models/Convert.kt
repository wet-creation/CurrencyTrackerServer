package com.mycompany.app.servercurrencytracker.restapi.models

data class Convert(
    val timestamp: Long,
    val rate: Double,
    val response: Double
)

