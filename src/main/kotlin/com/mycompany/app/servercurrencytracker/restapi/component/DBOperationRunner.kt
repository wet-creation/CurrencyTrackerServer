package com.mycompany.app.servercurrencytracker.restapi.component

import com.mycompany.app.servercurrencytracker.Const
import com.mycompany.app.servercurrencytracker.receiving.sources.GeckoApi
import com.mycompany.app.servercurrencytracker.restapi.repositories.RatesRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.crypto.CryptoCurrancyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Component
class DBOperationRunner(
    @Autowired
    val cryptoCurrancyRepository: CryptoCurrancyRepository,
    @Autowired
    val ratesRep: RatesRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
//        val geckoApi = Retrofit.Builder()
//            .baseUrl(Const.GECKO_API_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(GeckoApi::class.java)
//        val cryptoResponse = geckoApi.getCoins("USD").execute()
//        val cryptoList = cryptoResponse.body()!!
//        cryptoCurrancyRepository.saveAll(cryptoList.map { it.toCryptoDetails() })

//        val cryptoResponseList = coinPaprikaApi.getCoins().execute().body()!!
//        for (index in 28..100) {
//            val cryptoResponse = cryptoResponseList[index]
//            val response = coinPaprikaApi.getCoin(cryptoResponse.id).execute()
//            println(response.raw())
//            val cryptoResponseDetails = response.body()!!
//            cryptoCurrancyRepository.save(cryptoResponseDetails.toCryptoCurrancy())


        //------------------------

        //currenciesRepository.saveAll(currenciesName.body()!!.toCurrenciesName())

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
