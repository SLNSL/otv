package com.example.otv_processing.messaging

import com.example.otv_processing.exception.MessageAcknowledgeException
import com.example.otv_processing.service.TelegramService
import com.example.otv_processing.util.SendMessageBuilder
import com.example.otv_processing.util.getConnectionFactory
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Component
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage

private val queueName = "para_queue";

@Deprecated("")
class ParaQueueConsumer(
    private val telegramService: TelegramService
)  {


     fun onMessage(m: Message?) {
        val message = (m as TextMessage).text
        val isAcknowledge = true
        try {
            val jsonMessage = JSONObject(message)

            val chatId = jsonMessage.get("chatId") ?: throw JSONException("chatId is null")
            val paraList = jsonMessage.get("paraList") ?: throw JSONException("paraList is null")
            val teacherList = jsonMessage.get("teacherList") ?: throw JSONException("teacherList is null")

            var answerText = """
                Список предметов:
                $paraList
                
                Список преподавателей:
                $teacherList
            """.trimIndent()
            var answerSendMessage = SendMessageBuilder.builder()
                .message(answerText)
                .build(chatId.toString().toLong())
            telegramService.execute(answerSendMessage)

            answerText = """
                Пожалуйста оформи расписание пар, строго в виде:
                
                Расписание:
                Имя предмета; ФИО преподавателя;
                [Дата и время пар формата YYYY-MM-DD HH:MM:SS.ffffff
                Дата и время пар формата YYYY-MM-DD HH:MM:SS.ffffff
                Дата и время пар формата YYYY-MM-DD HH:MM:SS.ffffff];
                
                Например:
                
                Расписание:
                СОА; Егошин А.В.;
                [2023-12-13 20:00:00.000000
                2023-12-14 20:00:00.000000
                2024-12-02 15:30:00.000000];
                ИБ; Маркина Т.А.;
                [2023-12-13 20:00:00.000000
                2023-12-14 20:00:00.000000
                2024-12-02 15:30:00.000000];
            """.trimIndent()
            answerSendMessage = SendMessageBuilder.builder()
                .message(answerText)
                .build(chatId.toString().toLong())
            telegramService.execute(answerSendMessage)


        } catch (e: JSONException) {
            e.printStackTrace()
        } finally {
            if (isAcknowledge) m.acknowledge() else {
                println("Полученние сообщения $message не будет подтверждено")
            }
        }

    }
}