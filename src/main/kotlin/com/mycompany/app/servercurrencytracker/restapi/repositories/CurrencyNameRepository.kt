package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.CurrencyName
import org.springframework.data.jpa.repository.JpaRepository

interface CurrencyNameRepository: JpaRepository<CurrencyName, String>