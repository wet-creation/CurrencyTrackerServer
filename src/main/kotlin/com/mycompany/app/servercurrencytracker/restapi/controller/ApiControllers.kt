package com.mycompany.app.servercurrencytracker.restapi.controller

import ApiError
import com.mycompany.app.servercurrencytracker.restapi.models.Convert
import com.mycompany.app.servercurrencytracker.restapi.models.CurrencyName
import com.mycompany.app.servercurrencytracker.restapi.models.Rate
import com.mycompany.app.servercurrencytracker.restapi.repositories.CurrencyNameRepository
import com.mycompany.app.servercurrencytracker.restapi.repositories.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.time.Duration.Companion.seconds

@RestController
class ApiControllers(
    @Autowired
    private val ratesRepository: RatesRepository,
    @Autowired
    private val currenciesRepository: CurrencyNameRepository
) {

    @GetMapping(value = ["/"])
    fun getPages() = "Welcome"

    @GetMapping(value = ["/latest"])
    fun getRates() = ratesRepository.findLatestRate()

    @GetMapping(value = ["/latest/{symbol}"])
    fun getRatesBySymbol(@PathVariable symbol: String): ResponseEntity<*> {
        val rates: Rate? = ratesRepository.findRateBySymbol(symbol)
        return if (rates != null) {
            ResponseEntity.ok(rates)
        } else {
            val errorMessage = "Currency was not found by symbol=$symbol"
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.NotFound(System.currentTimeMillis(), HttpStatus.NOT_FOUND, errorMessage))
        }
    }

    @GetMapping(value = ["/convert"])
    fun convert(
        @RequestParam(required = true) value: Double?,
        @RequestParam(required = true) from: String?,
        @RequestParam(required = true) to: String?,
    ): ResponseEntity<*>? {
        if (value == null || from == null || to == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "Some value is null"
                    )
                )
        val fromRate = ratesRepository.findRateBySymbol(from)
        val timestamp = fromRate?.timestamp
        val rateFrom =
            fromRate?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "\"From\" is inncorect"
                    )
                )
        val rateTo =
            ratesRepository.findRateBySymbol(to)?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "\"To\" is inncorect"
                    )
                )

        val rate = rateTo/rateFrom
        val response = rate * value

        return ResponseEntity.status(HttpStatus.OK)
            .body(
               Convert(timestamp!!, rate, response)
            )
    }

    @GetMapping(value = ["/historical/{date}"])
    fun getRatesByDate(
        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") date: String,
        @RequestParam(required = false) symbol: String? = null
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val rates: List<Rate>? = ratesRepository.findRateByDate(timestamp, symbol)
            return if (rates != null) {
                ResponseEntity.ok(rates)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            HttpStatus.NOT_FOUND,
                            "Currency was not found by date=$date"
                        )
                    )
            }
        } catch (e: DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "Incorrect date, the format is dd-MM-yyyy"
                    )
                )
        }
    }

    @GetMapping(value = ["/historical"])
    fun getTimeSeries(
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") startDate: String,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") endDate: String,
        @RequestParam(required = false) symbol: String? = null
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        try {
            val parsedStart = LocalDate.parse(startDate, dateFormat)
            val parsedEnd = LocalDate.parse(endDate, dateFormat)
            val timestampStart = parsedStart.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val timestampEnd = parsedEnd.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val rates: List<Rate>? = ratesRepository.findRateByRangeDate(timestampStart, timestampEnd, symbol)
            return if (rates != null) {
                ResponseEntity.ok(rates)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            HttpStatus.NOT_FOUND,
                            "Currency was not found by dateStart=$startDate nor dateEnd=$endDate "
                        )
                    )
            }
        } catch (e: DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "Incorrect date, the format is dd-MM-yyyy"
                    )
                )
        }
    }

    @GetMapping(value = ["/currencies"])
    fun getCurrencies(@RequestParam(required = false) symbol: String? = null) =
        currenciesRepository.findCurrencyBySymbol(symbol)

    @GetMapping(value = ["/error"])
    fun getError() = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.Unexpected())
}
