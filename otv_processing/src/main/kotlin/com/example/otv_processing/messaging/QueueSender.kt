package com.example.otv_processing.messaging

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.json.JSONObject
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import javax.jms.Session.AUTO_ACKNOWLEDGE

@Slf4j
class QueueSender {

    private val createQueueName = "create_schedule_queue"
    private val changeQueueName = "change_schedule_queue"

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

    private fun getConnectionFactory() = SQSConnectionFactory(
        ProviderConfiguration(),
        AmazonSQSClientBuilder.standard()
            .withRegion("ru-central1")
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    "https://message-queue.api.cloud.yandex.net",
                    "ru-central1"
                )
            )
    )
}