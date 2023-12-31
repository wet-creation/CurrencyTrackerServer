package com.mycompany.app.servercurrencytracker.receiving.sources

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.dto.fiat.CurrenciesResponse
import com.mycompany.app.servercurrencytracker.receiving.dto.fiat.Latest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenExchangeRatesApi {
    @GET("latest.json")
    fun getCurrenciesLatest(@Query("app_id") apiKey:String = Const.OPEN_EXCHANGE_RATES_API): Call<Latest>

    @GET("currencies.json")
    fun getCurrenciesName(@Query("app_id") apiKey:String = Const.OPEN_EXCHANGE_RATES_API): Call<CurrenciesResponse>
}