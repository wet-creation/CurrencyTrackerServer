package com.mycompany.app.servercurrencytracker.scheduling

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.sources.OpenExchangeRatesApi
import com.mycompany.app.servercurrencytracker.restapi.models.toCurrenciesName
import com.mycompany.app.servercurrencytracker.restapi.models.toRate
import com.mycompany.app.servercurrencytracker.restapi.repositories.CurrencyNameRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["scheluding.enabled"], matchIfMissing = true)
class Schedule ()

@Component
class Jobs(
    @Autowired
    val ratesRepository: RatesRepository,
    @Autowired
    val currenciesRepository: CurrencyNameRepository
) {
    val openExchangeRatesApi = Retrofit.Builder()
        .baseUrl(Const.OPEN_EXCHANGE_RATES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenExchangeRatesApi::class.java)
    @Scheduled(cron = "\${getAndPutCurrancyNames.delay}")
    fun getAndPutCurrancyNames() {
        val currenciesName = openExchangeRatesApi.getCurrenciesName().execute()
        println(currenciesName.body().toString())
        currenciesRepository.saveAll(currenciesName.body()!!.toCurrenciesName())
    }
    @Scheduled(cron = "\${getAndPutRates.delay}")
    fun getAndPutRates() {
        val currenciesName = openExchangeRatesApi.getCurrenciesLatest().execute()
        println(currenciesName.body().toString())
        ratesRepository.saveAll(currenciesName.body()!!.toRate())
    }
    @Scheduled(cron = "* * * * * *")
    fun test(){
        println("Scheduled on")
    }
}