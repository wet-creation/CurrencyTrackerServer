package com.mycompany.app.servercurrencytracker.restapi.repositories.crypto

import com.mycompany.app.servercurrencytracker.restapi.models.crypto.Crypto
import org.springframework.data.jpa.repository.JpaRepository

interface CryptoCurrancyRepository: JpaRepository<Crypto,String>