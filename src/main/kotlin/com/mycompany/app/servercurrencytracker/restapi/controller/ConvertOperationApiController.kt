package com.mycompany.app.servercurrencytracker.restapi.controller

import ApiError
import com.mycompany.app.servercurrencytracker.restapi.models.Convert
import com.mycompany.app.servercurrencytracker.restapi.repositories.CryptoFiatRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ConvertOperationApiController(
    @Autowired
    private val cryptoFiatRateRepository: CryptoFiatRepository
) {
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
                        "Some value is null"
                    )
                )
        val lastRateFrom = cryptoFiatRateRepository.findLatest(from)
        val lastRateTo = cryptoFiatRateRepository.findLatest(to)
        val rateFrom =
            lastRateFrom?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        "\"From\" is inncorect"
                    )
                )
        val rateTo =
            lastRateTo?.rate ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiError.BadRequest(
                        System.currentTimeMillis(),
                        "\"To\" is inncorect"
                    )
                )
        val timestamp = if (lastRateTo.timestamp > lastRateFrom.rate) lastRateTo.timestamp else lastRateFrom.timestamp
        val rate = rateFrom / rateTo
        val response = rate * value

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                Convert(timestamp, rate, response)
            )
    }
}