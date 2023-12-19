package com.mycompany.app.servercurrencytracker.scheduling

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.sources.GeckoApi
import com.mycompany.app.servercurrencytracker.receiving.sources.OpenExchangeRatesApi
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.toCurrenciesName
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.toRate
import com.mycompany.app.servercurrencytracker.restapi.repositories.currancy.CurrencyNameRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.currancy.RatesRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.crypto.CryptoCurrancyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["scheluding.enabled"], matchIfMissing = true)
class Schedule

@Component
class Jobs(
    @Autowired
    val ratesRepository: RatesRepository,
    @Autowired
    val currenciesRepository: CurrencyNameRepository,
    @Autowired
    val cryptoRepository: CryptoCurrancyRepository
) {
    val openExchangeRatesApi = Retrofit.Builder()
        .baseUrl(Const.OPEN_EXCHANGE_RATES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenExchangeRatesApi::class.java)
    val geckoApi = Retrofit.Builder()
        .baseUrl(Const.GECKO_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeckoApi::class.java)

    @Scheduled(cron = "\${getAndPutCurrancyNames.delay}")
    fun getAndPutCurrancyNames() {
        val currenciesName = openExchangeRatesApi.getCurrenciesName().execute().body()
        if (currenciesName == null) {
            println("Couldn't get currency Names at ${Date()}")
            return
        }
        println("Get Currency Names at ${Date()}")
        currenciesRepository.saveAll(currenciesName.toCurrenciesName())
    }

    @Scheduled(cron = "\${getAndPutRates.delay}")
    fun getAndPutRates() {
        val currenciesResponse = openExchangeRatesApi.getCurrenciesLatest().execute()
        val currencies = currenciesResponse.body()
        if (currencies == null) {
            println("Couldn't get Currency Rates and Put at ${Date()}")
            return
        }
        val modifiedRates = currencies.rates.mapValues { (_, value) -> 1.0 / value }
        val modifiedLatest = currencies.copy(rates = modifiedRates)
        println("Get Currency Rates and Put at ${Date()}")
        ratesRepository.saveAll(modifiedLatest.toRate())
    }

    @Scheduled(cron = "\${getAndPutCrypto.delay}")
    fun getAndPutCrypto() {
        val cryptoResponse = geckoApi.getCoins("USD").execute()
        val cryptoList = cryptoResponse.body()
        if (cryptoList == null) {
            println("Couldn't get Crypto Rates and Put at ${Date()}")
            return
        }
        println("Get Crypto Rates and Put at ${Date()}")
        cryptoRepository.saveAll(cryptoList.map { it.toCryptoDetails() })

    }
}