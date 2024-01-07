package com.mycompany.app.servercurrencytracker.scheduling

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.sources.GeckoApi
import com.mycompany.app.servercurrencytracker.receiving.sources.OpenExchangeRatesApi
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.toCurrenciesName
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.toRate
import com.mycompany.app.servercurrencytracker.restapi.repositories.crypto.CryptoCurrancyRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.currancy.CurrencyNameRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.currancy.RatesRepository
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit


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
    private final val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Adjust as needed
        .readTimeout(30, TimeUnit.SECONDS) // Adjust as needed
        .writeTimeout(30, TimeUnit.SECONDS) // Adjust as needed
        .build()
    val openExchangeRatesApi = Retrofit.Builder()
        .baseUrl(Const.OPEN_EXCHANGE_RATES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(OpenExchangeRatesApi::class.java)
    val geckoApi = Retrofit.Builder()
        .baseUrl(Const.GECKO_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(GeckoApi::class.java)

    @Scheduled(cron = "\${getAndPutCurrancyNames.delay}")
    fun getAndPutCurrencyNames() {
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
        val currentTime = modifiedLatest.timestamp
        val timestamp1d = currentTime - 86400
        val timestamp7d = currentTime - 86400 * 7
        val timestamp30d = currentTime - 86400 * 30
        val rates = modifiedLatest.toRate().map {
            it.copy(
                _24h = ratesRepository.findRateByDateAndSymbol(timestamp1d, it.symbol),
                _7d = ratesRepository.findRateByDateAndSymbol(timestamp7d, it.symbol),
                _1m = ratesRepository.findRateByDateAndSymbol(timestamp30d, it.symbol)
            )
        }
        println("Get Currency Rates and Put at ${Date()}")
        ratesRepository.saveAll(rates)
    }

    @Scheduled(cron = "\${getAndPutCrypto.delay}")
    fun getAndPutCrypto() {
        val cryptoResponse = geckoApi.getCoins("USD").execute()
        val cryptoList = cryptoResponse.body()
        if (cryptoList == null) {
            println("Couldn't get Crypto Rates and Put at ${Date()}")
            return
        }
        val currentUtcTime = LocalDateTime.now(ZoneOffset.UTC)
        val timestamp7d = currentUtcTime.minusDays(7).toEpochSecond(ZoneOffset.UTC)
        val timestamp30d = currentUtcTime.minusDays(30).toEpochSecond(ZoneOffset.UTC)
        val _cryptos = cryptoList.map { it.toCryptoDetails() }
        val cryptos = _cryptos.map {
            it.copy(
                _7d = cryptoRepository.getAvgBySymbolAndDate(timestamp7d, it.symbol),
                _1m = cryptoRepository.getAvgBySymbolAndDate(timestamp30d, it.symbol)
            )
        }
        println("Get Crypto Rates and Put at ${Date()}")
        cryptoRepository.saveAll(cryptos)

    }
}