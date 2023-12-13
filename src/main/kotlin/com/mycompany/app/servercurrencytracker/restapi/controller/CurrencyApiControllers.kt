package com.mycompany.app.servercurrencytracker.restapi.controller

import ApiError
import com.mycompany.app.servercurrencytracker.restapi.models.Convert
import com.mycompany.app.servercurrencytracker.restapi.models.currancy.CurrencyRate
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
    private val currencyRatesRepository: CurrencyRatesRepository
) {

    @GetMapping(value = ["/"])
    fun getPages() = "Welcome"

    @GetMapping(value = ["/latest"])
    fun getRates() = currencyRatesRepository.findLatestRate()

    @GetMapping(value = ["/latest/{symbol}"])
    fun getRatesBySymbol(@PathVariable symbol: String): ResponseEntity<*> {
        val rates: CurrencyRate? = currencyRatesRepository.findRateBySymbol(symbol)
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
        val timestamp =  currencyRatesRepository.findRateBySymbol(from)?.timestamp
        val rateFrom =
            currencyRatesRepository.findRateBySymbol(from)?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        HttpStatus.BAD_REQUEST,
                        "\"From\" is inncorect"
                    )
                )
        val rateTo =
            currencyRatesRepository.findRateBySymbol(to)?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val rates: List<CurrencyRate>? = currencyRatesRepository.findRateByDate(timestamp, symbol)
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
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsedStart = LocalDate.parse(startDate, dateFormat)
            val parsedEnd = LocalDate.parse(endDate, dateFormat)
            val timestampStart = parsedStart.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val timestampEnd = parsedEnd.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val rates: List<CurrencyRate>? = currencyRatesRepository.findRateByRangeDate(timestampStart, timestampEnd, symbol)
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

    @GetMapping(value = ["/error"])
    fun getError() = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.Unexpected())

}
