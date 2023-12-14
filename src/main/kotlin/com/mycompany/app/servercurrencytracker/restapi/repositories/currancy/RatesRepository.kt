package com.mycompany.app.servercurrencytracker.restapi.repositories.currancy

import com.mycompany.app.servercurrencytracker.restapi.models.currancy.Rate
import org.springframework.data.jpa.repository.JpaRepository

interface RatesRepository: JpaRepository<Rate, Long>