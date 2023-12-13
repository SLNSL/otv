package com.example.otv_processing.service

import com.example.otv_processing.service.abstraction.MessageProcessService
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
@Slf4j
class TelegramService(
    @Value("\${bot.token}") botToken: String = "6761629168:AAHOyhJ2IdSuM2yA7kn6POzJ9clByVMl1M8",
    @Value("\${bot.name}") val botName: String = "Deadline Bot",
    private val messageProcessService: MessageProcessService
) : TelegramLongPollingBot(botToken) {

    @PostConstruct
    fun init() {
        println(botName)
    }

    override fun getBotUsername(): String {
        return botName
    }

    override fun onUpdateReceived(update: Update) {
        log.info  { () -> "ЗАПРОС ${update.message.text}"}
        val sendMessage = messageProcessService.process(update)
        try {
            if (sendMessage != null) execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

}