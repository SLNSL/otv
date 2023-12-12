package com.example.otv_notification.consumer

import com.example.otv_notification.service.TelegramSender
import com.example.otv_notification.service.abstraction.NotificationsService
import com.example.otv_notification.util.getConnectionFactory
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Component
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage

private val queueName = "short_command_queue"
@Component
@Slf4j
class ShortCommandConsumer(
    private val notificationsService: NotificationsService,
    private val telegramSender: TelegramSender
) : MessageListener {

    @PostConstruct
    fun init() {
        val connection = getConnectionFactory().createConnection()
        val session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
        val queue = session.createQueue(queueName);
        val consumer = session.createConsumer(queue)
        consumer.messageListener = this
        connection.start()
        Thread.sleep(1000)
    }

    override fun onMessage(m: Message?) {
        val message = (m as TextMessage).text
        try {
            val jsonMessage = JSONObject(message)

            val chatId = jsonMessage.get("chatId") ?: throw JSONException("chatId is null")
            val command = jsonMessage.get("command") ?: throw JSONException("command is null")
            handleCommand(chatId.toString(), command.toString())
        } catch (e: JSONException) {
            SqmNode.log.error(
                "При парсе полученного сообщения в JSON произошла ошибка. Сообщение некорректное и все равно будет удалено из очереди: $message",
                e
            )
        } finally {
            m.acknowledge()
        }
    }

    private fun handleCommand(chatId: String, command: String) {
        if (command == "send_now") {
            sendNowCommand(chatId)
        }
    }

    private fun sendNowCommand(chatId: String) {
        val count = notificationsService.nowSchedule()
        telegramSender.send(chatId, "Успешно отправлены $count уведомления(-ий)")
    }
}