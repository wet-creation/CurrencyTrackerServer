package com.mycompany.app.servercurrencytracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class ServerCurrencyTrackerApplication

fun main(args: Array<String>) {

	runApplication<ServerCurrencyTrackerApplication>(*args)
}
