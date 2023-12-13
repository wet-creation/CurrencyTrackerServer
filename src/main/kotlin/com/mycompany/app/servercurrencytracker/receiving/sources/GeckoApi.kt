package com.mycompany.app.servercurrencytracker.receiving.sources

import com.mycompany.app.servercurrencytracker.receiving.dto.crypto.CryptoCurrencyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeckoApi {
    @GET("coins/markets")
    fun getCoins(@Query("vs_currency") base: String): Call<List<CryptoCurrencyResponse>>
}