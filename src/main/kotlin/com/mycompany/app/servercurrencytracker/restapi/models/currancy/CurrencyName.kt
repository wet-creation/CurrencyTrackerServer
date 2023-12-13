package com.mycompany.app.servercurrencytracker.restapi.models.currancy

import com.mycompany.app.servercurrencytracker.receiving.dto.fiat.CurrenciesResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.lang.reflect.Field

@Entity
data class CurrencyName(
    @Id
    var symbol: String,
    @Column
    var name: String
){
    constructor(): this("Usd","United States Dollar")
}

fun CurrenciesResponse.toCurrenciesName(): List<CurrencyName> {
    val currencyNameList = mutableListOf<CurrencyName>()

    // Get all fields in the CurrenciesResponse class
    val fields: Array<Field> = CurrenciesResponse::class.java.declaredFields

    for (field in fields) {
        // Ensure that the field is accessible
        field.isAccessible = true

        // Get the currency code and name using reflection
        val currencyCode = field.name
        val currencyName = field.get(this) as String

        // Create CurrencyName object and add to the list
        val currencyNameObject = CurrencyName(symbol = currencyCode, name = currencyName)
        currencyNameList.add(currencyNameObject)
    }

    return currencyNameList
}