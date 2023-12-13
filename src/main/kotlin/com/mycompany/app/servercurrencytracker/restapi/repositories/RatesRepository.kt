package com.mycompany.app.servercurrencytracker.restapi.repositories

import com.mycompany.app.servercurrencytracker.restapi.models.Rate
import org.springframework.data.jpa.repository.JpaRepository

interface RatesRepository: JpaRepository<Rate, Long>