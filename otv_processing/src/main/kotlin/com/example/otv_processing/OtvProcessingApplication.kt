package com.example.otv_processing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


@SpringBootApplication
class OtvProcessingApplication

fun main(args: Array<String>) {
    runApplication<OtvProcessingApplication>(*args)
}
