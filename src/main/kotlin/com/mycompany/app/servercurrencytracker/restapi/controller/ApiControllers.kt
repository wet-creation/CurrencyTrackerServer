package com.mycompany.app.servercurrencytracker.restapi.controller

import ApiError
import com.mycompany.app.servercurrencytracker.restapi.models.Rate
import com.mycompany.app.servercurrencytracker.restapi.repositories.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RestController
class ApiControllers(
    @Autowired
    private val ratesRepository: RatesRepository
) {

    @GetMapping(value = arrayOf("/"))
    fun getPages() = "Welcome"

    @GetMapping(value = arrayOf("/latest"))
    fun getRates() = ratesRepository.findLatestRate()

    @GetMapping(value = arrayOf("/latest/{symbol}"))
    fun getRatesBySymbol(@PathVariable symbol: String): ResponseEntity<*> {
        val rates: List<Rate>? = ratesRepository.findRateBySymbol(symbol)
        return if (rates != null) {
            ResponseEntity.ok(rates)
        } else {
            val errorMessage = "Currency was not found by symbol=$symbol"
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.NotFound(System.currentTimeMillis(), HttpStatus.NOT_FOUND, errorMessage))
        }
    }

    @GetMapping(value = ["/historical/{date}"])
    fun getRatesByDate(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") date: String): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val rates: List<Rate>? = ratesRepository.findRateByDate(timestamp)
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
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") dateStart: String,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") dateEnd: String
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        try {
            val parsedStart = LocalDate.parse(dateStart, dateFormat)
            val parsedEnd = LocalDate.parse(dateEnd, dateFormat)
            val timestampStart = parsedStart.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val timestampEnd = parsedEnd.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val rates: List<Rate>? = ratesRepository.findRateByRangeDate(timestampStart, timestampEnd)
            return if (rates != null) {
                ResponseEntity.ok(rates)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            HttpStatus.NOT_FOUND,
                            "Currency was not found by dateStart=$dateStart nor dateEnd=$dateEnd "
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
}
