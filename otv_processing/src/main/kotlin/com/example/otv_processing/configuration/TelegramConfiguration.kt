package com.example.otv_processing.configuration

import com.example.otv_processing.service.TelegramService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramConfiguration {
    @Bean
    fun telegramBotsApi(telegramService: TelegramService): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(telegramService)
        }
}