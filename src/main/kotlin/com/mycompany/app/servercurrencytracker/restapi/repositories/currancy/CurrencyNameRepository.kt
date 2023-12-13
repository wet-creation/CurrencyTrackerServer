package com.mycompany.app.servercurrencytracker.restapi.repositories.currancy

import com.mycompany.app.servercurrencytracker.restapi.models.currancy.CurrencyName
import org.springframework.data.jpa.repository.JpaRepository

interface CurrencyNameRepository: JpaRepository<CurrencyName, String>