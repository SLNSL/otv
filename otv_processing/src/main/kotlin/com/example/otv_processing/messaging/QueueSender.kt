package com.example.otv_processing.messaging

import com.example.otv_processing.util.getConnectionFactory
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.json.JSONObject
import javax.jms.Session.AUTO_ACKNOWLEDGE

@Slf4j
class QueueSender {

    private val createQueueName = "create_schedule_queue"
    private val changeQueueName = "change_schedule_queue"
    private val paraQueueName = "para_queue"
    private val commandQueueName = "short_command_queue"

    fun sendCreateMessage(chatId: String, group: String, noticePeriod: String) {
        val jsonObject = JSONObject()
        jsonObject.put("chatId", chatId)
        jsonObject.put("group", group)
        jsonObject.put("noticePeriod", noticePeriod)
        send(jsonObject, createQueueName)
        log.info("Отправлено сообщение в топик create_schedule_queue: $jsonObject")

    }

    fun sendDeleteMessage(chatId: String) {
        val jsonObject = JSONObject()

        jsonObject.put("notification", false)
        jsonObject.put("chatId", chatId)
        send(jsonObject, changeQueueName)
        log.info("Отправлено сообщение в топик change_schedule_queue: $jsonObject")
    }

    fun sendChangeMessage(chatId: String, group: String, noticePeriod: String) {
        val jsonObject = JSONObject()

        jsonObject.put("chatId", chatId)
        jsonObject.put("group", group)
        jsonObject.put("noticePeriod", noticePeriod)
        send(jsonObject, changeQueueName)
        log.info("Отправлено сообщение в топик change_schedule_queue: $jsonObject")
    }

    fun sendGetTeachersAndSubjects(chatId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("chatId", chatId)
        jsonObject.put("type", "get")
        send(jsonObject, paraQueueName)
        log.info("Отправлено сообщение в топик $paraQueueName: $jsonObject")
    }

    fun sendShortCommand(chatId: String, command: String) {
        val jsonObject = JSONObject()
        jsonObject.put("chatId", chatId)
        jsonObject.put("command", command)
        send(jsonObject, commandQueueName)
        log.info("Отправлено сообщение в топик $command: $jsonObject")
    }

    fun sendNewParas(chatId: String, parasData: String, group: String, groupChatIdsToNoticePeriod: List<Pair<Long?, Int?>>) {
        val jsonObject = JSONObject()
        jsonObject.put("chatId", chatId)
        jsonObject.put("type", "create")
        jsonObject.put("data", parasData)
        jsonObject.put("group", group)
        jsonObject.put("groupChatIdsToNoticePeriod", groupChatIdsToNoticePeriod)
        send(jsonObject, paraQueueName)
        log.info("Отправлено сообщение в топик $paraQueueName: $jsonObject")
    }

    private fun send(json: JSONObject, queueName: String) {
        val connectionFactory = getConnectionFactory()

        val connection = connectionFactory.createConnection()
        val client = connection.wrappedAmazonSQSClient

        if( !client.queueExists(queueName) ) {
            client.createQueue( queueName )
        }

        val session = connection.createSession(false, AUTO_ACKNOWLEDGE)
        val queue = session.createQueue(queueName)

        val producer = session.createProducer(queue)
        val message = session.createTextMessage(json.toString())
        producer.send(message)
    }
}