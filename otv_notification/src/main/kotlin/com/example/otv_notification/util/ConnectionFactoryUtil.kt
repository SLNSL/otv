package com.example.otv_notification.util

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSClientBuilder

fun getConnectionFactory() = SQSConnectionFactory(
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