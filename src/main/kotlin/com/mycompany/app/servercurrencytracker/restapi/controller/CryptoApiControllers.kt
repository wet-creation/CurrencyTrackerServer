package com.mycompany.app.servercurrencytracker.restapi.controller

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import com.mycompany.app.servercurrencytracker.restapi.repositories.CryptoFiatRepository
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
    val cryptoCurrancyRepository: CryptoCurrancyRepository,
    @Autowired
    val cryptoFiatRepository: CryptoFiatRepository
) {
    @GetMapping(value = ["/latest/crypto"])
    fun getCryptos(@RequestParam(required = false) baseCurrency: String = "USD"): ResponseEntity<*> {
        val cryptoList = cryptoCurrancyRepository.findLatest()?.sortedBy { it.market_cap_rank }?.subList(0, 100)
            ?: return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
                .body(
                    ApiError.InternalServerError()
                )
        return if (baseCurrency != "USD")
            ResponseEntity.ok(getBaseCrypto(cryptoList, baseCurrency))
        else
            ResponseEntity.ok(cryptoList)
    }

    private fun getBaseCrypto(cryptoList: List<Crypto>, baseCurrency: String): List<Crypto>? {
        val base = cryptoFiatRepository.findLastRate(baseCurrency) ?: return null
        val baseList = mutableListOf<Crypto>()
        cryptoList.forEach { crypto ->
            val baseRate = crypto.current_price / base.rate
            val baserCrypto = crypto.copy(current_price = baseRate)
            baserCrypto.id = crypto.id
            baseList.add(baserCrypto)
        }
        return baseList
    }

    @GetMapping(value = ["/latest/crypto/{symbol}"])
    fun getCrypto(
        @PathVariable symbol: String,
        @RequestParam(required = false) baseCurrency: String = "USD"
    ): ResponseEntity<*> {
        val crypto = cryptoCurrancyRepository.findLatestBySymbol(symbol) ?: return ResponseEntity.status(
            HttpStatus.NOT_FOUND
        )
            .body(
                ApiError.BadRequest(
                    System.currentTimeMillis(),
                    "$symbol not found"
                )
            )
        if (baseCurrency != "USD") {
            val cryptoBase = cryptoFiatRepository.findLastRate(baseCurrency) ?: return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                ApiError.InternalServerError()
            )
            val baseRate = crypto.current_price / cryptoBase.rate
            val returnCrypto = crypto.copy(current_price = baseRate)
            returnCrypto.id = crypto.id
            return ResponseEntity.ok(returnCrypto)
        }
        return ResponseEntity.ok(crypto)

    }

    @GetMapping(value = ["/historical/{date}/crypto"])
    fun getCryptoByDate(
        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") date: String,
        @RequestParam(required = false) symbol: String? = null,
        @RequestParam(required = false) baseCurrency: String = "USD"
    ): ResponseEntity<*> {
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val parsed = LocalDate.parse(date, dateFormat)
            val timestamp = parsed.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            val cryptos: List<Crypto> =
                cryptoCurrancyRepository.findCryptoByDate(timestamp, symbol)?.sortedBy { it.last_updated }
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                            ApiError.NotFound(
                                System.currentTimeMillis(),
                                "Crypto was not found by date=$date"
                            )
                        )
            return if (baseCurrency != "USD")
                ResponseEntity.ok(getBaseCrypto(cryptos, baseCurrency))
            else
                ResponseEntity.ok(cryptos)
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

    @GetMapping(value = ["/historical/crypto"])
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
            val cryptos: List<Crypto> =
                cryptoCurrancyRepository.findCryptoByRangeDate(timestampStart, timestampEnd, symbol)
                    ?.sortedBy { it.last_updated } ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        ApiError.NotFound(
                            System.currentTimeMillis(),
                            "Crypto was not found by dateStart=$startDate nor dateEnd=$endDate "
                        )
                    )
            return if (baseCurrency != "USD") {
                ResponseEntity.ok(getBaseCrypto(cryptos, baseCurrency))
            } else {
                ResponseEntity.ok(cryptos)
            }
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
}