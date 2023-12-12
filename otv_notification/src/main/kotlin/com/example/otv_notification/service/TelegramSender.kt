package com.example.otv_notification.service

import com.example.otv_notification.entity.Notification
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class TelegramSender(
    @Value("\${bot.token}") botToken: String = "6761629168:AAHOyhJ2IdSuM2yA7kn6POzJ9clByVMl1M8",
    @Value("\${bot.name}") val botName: String = "Deadline Bot"
) : TelegramLongPollingBot(botToken) {

    private val notificationMessage = """
        Напоминание о лабораторной работе! 
        Предмет: %s
        Преподаватель: %s
        Когда: %s
    """.trimIndent()

    override fun getBotUsername(): String {
        return botName
    }

    override fun onUpdateReceived(update: Update) {

    }

    fun send(notification: Notification) {
        val message = notificationMessage.format(
            notification.subjectTeacher!!.subject!!.name,
            notification.subjectTeacher!!.teacher!!.name,
            notification.noticeDate!!.plusDays(notification.noticePeriod!!.toLong())
        )

        val sendMessage = SendMessage()
        sendMessage.chatId = notification.chatId!!
        sendMessage.text = message
        execute(sendMessage)
    }

    fun send(chatId: String, message: String) {
        val sendMessage = SendMessage()
        sendMessage.chatId = chatId
        sendMessage.text = message
        execute(sendMessage)
    }

}