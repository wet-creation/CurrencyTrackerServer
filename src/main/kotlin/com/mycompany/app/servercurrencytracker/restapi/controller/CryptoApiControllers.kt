package com.mycompany.app.servercurrencytracker.restapi.controller

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import com.mycompany.app.servercurrencytracker.restapi.repositories.crypto.CryptoCurrancyRepository
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
class CryptoApiControllers(
    @Autowired
    val cryptoCurrancyRepository: CryptoCurrancyRepository
) {
    @GetMapping(value = ["/latest/cryptos"])
    fun getCryptos() = cryptoCurrancyRepository.findLatest()?.subList(0, 100)?.sortedBy { it.market_cap_rank }

    @GetMapping(value = ["/latest/cryptos/{symbol}"])
    fun getCrypto(@PathVariable symbol: String) = cryptoCurrancyRepository.findLatestBySymbol(symbol) ?:ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ApiError.BadRequest(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND,
                "$symbol not found"
            )
        )

    @GetMapping(value = ["/historical/{date}/crypto"])
    fun getCryptoByDate(
        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") date: String,
        @RequestParam(required = false) symbol: String? = null
    ) :ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val cryptos: List<Crypto>? = cryptoCurrancyRepository.findCryptoByDate(timestamp, symbol)?.sortedBy { it.last_updated }
            return if (cryptos != null) {
                ResponseEntity.ok(cryptos)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            HttpStatus.NOT_FOUND,
                            "Crypto was not found by date=$date"
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

    @GetMapping(value = ["/historical/crypto"])
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
            val cryptos: List<Crypto>? = cryptoCurrancyRepository.findCryptoByRangeDate(timestampStart, timestampEnd, symbol)?.sortedBy { it.last_updated }
            return if (cryptos != null) {
                ResponseEntity.ok(cryptos)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            HttpStatus.NOT_FOUND,
                            "Crypto was not found by dateStart=$startDate nor dateEnd=$endDate "
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