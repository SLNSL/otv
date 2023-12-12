package com.example.otv_notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class OtvNotificationApplication

fun main(args: Array<String>) {
    runApplication<OtvNotificationApplication>(*args)
}
