package com.mycompany.app.servercurrencytracker.restapi.component

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.dto.Latest
import com.mycompany.app.servercurrencytracker.receiving.sources.OpenExchangeRatesApi
import com.mycompany.app.servercurrencytracker.restapi.models.toCurrenciesName
import com.mycompany.app.servercurrencytracker.restapi.models.toRate
import com.mycompany.app.servercurrencytracker.restapi.repositories.CurrencyNameRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Component
class DBOperationRunner(
    @Autowired
    val ratesRepository: RatesRepository,
    @Autowired
    val currenciesRepository: CurrencyNameRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val openExchangeRatesApi = Retrofit.Builder()
            .baseUrl(Const.OPEN_EXCHANGE_RATES_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenExchangeRatesApi::class.java)
        val currenciesName = openExchangeRatesApi.getCurrenciesName().execute()
        currenciesRepository.saveAll(currenciesName.body()!!.toCurrenciesName())

        // val latest = openExchangeRatesApi.getCurrenciesLatest()
        // ratesRepository.saveAll(latest.execute().body()!!.toRatesModelList())

//        val ratesMap = mapOf(
//            "AED" to 3.6724,
//            "AFN" to 69.290389,
//            "ALL" to 94.537512,
//            "AMD" to 403.768708,
//            "ANG" to 1.803778,
//        )
//        val latest = Latest(base="USD", disclaimer="Usage subject to terms: https://openexchangerates.org/terms"," license=https://openexchangerates.org/license", ratesMap, timestamp=1702220000)
//
//        val rates = latest.toRate()
//        ratesRepository.saveAll(rates)
    }
}