package com.example.otv_notification.consumer

import com.example.otv_notification.service.abstraction.NotificationsService
import com.example.otv_notification.util.getConnectionFactory
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Component
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.Session.CLIENT_ACKNOWLEDGE
import javax.jms.TextMessage


private val queueName = "create_schedule_queue";

@Component
@Slf4j
class CreateQueueConsumer(
    private val notificationsService: NotificationsService
) : MessageListener {

    override fun onMessage(m: Message?) {
        val message = (m as TextMessage).text
        try {
            log.info("$queueName Message received: $message")
            val jsonMessage = JSONObject(message)

            val chatId = jsonMessage.get("chatId") ?: throw JSONException("chatId is null")
            val groupName = jsonMessage.get("group") ?: throw JSONException("group is null")
            val noticePeriod = jsonMessage.get("noticePeriod") ?: throw JSONException("noticePeriod is null")

            notificationsService.createNotifications(noticePeriod.toString(), groupName.toString(), chatId.toString())

        } catch (e: JSONException) {
            log.error("При парсе полученного сообщения в JSON произошла ошибка. Сообщение некорректное и все равно будет удалено из очереди: $message", e)
        }
        finally {
            m.acknowledge()
        }
    }

    @PostConstruct
    fun init() {
        val connection = getConnectionFactory().createConnection()
        val session = connection.createSession(false, CLIENT_ACKNOWLEDGE)
        val queue = session.createQueue(queueName);
        val consumer = session.createConsumer(queue)
        consumer.messageListener = this
        connection.start()
        Thread.sleep(1000)
    }
}
