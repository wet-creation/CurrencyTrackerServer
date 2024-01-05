package com.mycompany.app.servercurrencytracker.restapi.controller

import ApiError
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.CurrencyRate
import com.mycompany.app.servercurrencytracker.restapi.repositories.CryptoFiatRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.currancy.CurrencyRatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RestController
class CurrencyApiControllers(
    @Autowired
    private val currencyRatesRepository: CurrencyRatesRepository,
    @Autowired
    private val cryptoFiatRepository: CryptoFiatRepository
) {

    @GetMapping(value = ["/"])
    fun getPages() = "Welcome"

    @GetMapping(value = ["/latest"])
    fun getRates(@RequestParam(required = false) baseCurrency: String = "USD"): ResponseEntity<*> {
        val currancyList = currencyRatesRepository.findLatestRate()
        val baseRateCurrancyList =
            getBaseCurrancyList(currancyList, baseCurrency) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                    ApiError.NotFound(
                        System.currentTimeMillis(),
                        "Currency was not found by base currency=$baseCurrency"
                    )
                )
        return ResponseEntity.ok(baseRateCurrancyList)
    }

    @GetMapping(value = ["/latest/{symbol}"])
    fun getRatesBySymbol(
        @PathVariable symbol: String,
        @RequestParam(required = false) baseCurrency: String = "USD"
    ): ResponseEntity<*> {
        val rate: CurrencyRate? = currencyRatesRepository.findRateBySymbol(symbol)
        val rateBase = cryptoFiatRepository.findLatest(baseCurrency)
        return if (rate != null && rateBase != null) {
            val currencyRate = rate.rate / rateBase.rate
            val convertRate = rate.copy(rate = currencyRate)
            ResponseEntity.ok(convertRate)
        } else {
            val errorMessage = "Currency was not found by symbol=$symbol or baseCurrency=$symbol"
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.NotFound(System.currentTimeMillis(), errorMessage))
        }
    }

    @GetMapping(value = ["/historical/{date}"])
    fun getRatesByDate(
        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") date: String,
        @RequestParam(required = false) symbol: String? = null,
        @RequestParam(required = false) baseCurrency: String = "USD"
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val rates: List<CurrencyRate> = currencyRatesRepository.findRateByDate(timestamp, symbol)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            "Currency was not found by date=$date"
                        )
                    )
            val baseRateCurrancyList =
                getBaseCurrancyList(rates, baseCurrency) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiError.NotFound(
                        System.currentTimeMillis(),
                        "Currency was not found by base currency=$baseCurrency"
                    )
                )
            return ResponseEntity.ok(baseRateCurrancyList)
        } catch (e: DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        "Incorrect date, the format is dd-MM-yyyy"
                    )
                )
        }
    }

    @GetMapping(value = ["/historical"])
    fun getTimeSeries(
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") startDate: String,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") endDate: String,
        @RequestParam(required = false) symbol: String? = null,
        @RequestParam(required = false) baseCurrency: String = "USD"
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsedStart = LocalDate.parse(startDate, dateFormat)
            val parsedEnd = LocalDate.parse(endDate, dateFormat)
            val timestampStart = parsedStart.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val timestampEnd = parsedEnd.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val rates: List<CurrencyRate> =
                currencyRatesRepository.findRateByRangeDate(timestampStart, timestampEnd, symbol)
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                            ApiError.NotFound(
                                System.currentTimeMillis(),
                                "Currency was not found by dateStart=$startDate nor dateEnd=$endDate "
                            )
                        )
            val baseRateCurrancyList =
                getBaseCurrancyList(rates, baseCurrency) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiError.NotFound(
                        System.currentTimeMillis(),
                        "Currency was not found by base currency=$baseCurrency"
                    )
                )
            return ResponseEntity.ok(baseRateCurrancyList)
        } catch (e: DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        "Incorrect date, the format is dd-MM-yyyy"
                    )
                )
        }
    }

    @GetMapping(value = ["/error"])
    fun getError() = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.Unexpected())

    fun getBaseCurrancyList(currancyList: List<CurrencyRate>?, baseCurrancy: String): List<CurrencyRate>? {
        val baseRateCurrancy = cryptoFiatRepository.findLatest(baseCurrancy) ?: return null
        val listBaseCurranies = mutableListOf<CurrencyRate>()
        currancyList?.forEach { currency ->
            val currencyRate = currency.rate / baseRateCurrancy.rate
            listBaseCurranies.add(
               currency.copy(rate = currencyRate)
            )
        }
        return listBaseCurranies
    }

}
