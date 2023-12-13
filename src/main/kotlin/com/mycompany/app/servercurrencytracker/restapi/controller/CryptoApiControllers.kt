package com.mycompany.app.servercurrencytracker.restapi.controller

import com.mycompany.app.servercurrencytracker.restapi.repositories.crypto.CryptoCurrancyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CryptoApiControllers(
    @Autowired
    val cryptoCurrancyRepository: CryptoCurrancyRepository
) {
    @GetMapping(value = ["/cryptos"])
    fun getCryptos() = cryptoCurrancyRepository.findAll()

    @GetMapping(value = ["/cryptos/{id}"])
    fun getCrypto(@PathVariable id: String) = cryptoCurrancyRepository.findById(id)
}