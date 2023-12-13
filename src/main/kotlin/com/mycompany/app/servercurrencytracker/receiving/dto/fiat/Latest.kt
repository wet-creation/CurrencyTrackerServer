package com.mycompany.app.servercurrencytracker.receiving.dto.fiat

data class Latest(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Map<String, Double>,
    val timestamp: Long
)