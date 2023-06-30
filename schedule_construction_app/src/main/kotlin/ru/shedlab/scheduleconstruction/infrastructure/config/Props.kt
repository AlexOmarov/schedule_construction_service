package ru.shedlab.scheduleconstruction.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("")
data class Props(
    val messages: MessagesProps,
    val app: AppProps,
    val kafka: KafkaProps,
    val cache: CacheProps
) {
    data class RsocketProps(
        val uri: String
    )

    data class SchedulingProps(
        val threadPoolSize: Int,
        val stubScheduler: SchedulerProps,
    )

    data class SchedulerProps(
        val batchSize: Int,
        val lockMaxDuration: Duration
    )

    data class AppProps(
        val instance: String,
        val scheduler: SchedulingProps,
        val rsocket: RsocketProps
    )

    data class MessagesProps(
        val basename: String,
        val encoding: String
    )

    data class CacheProps(
        val defaultTime: Duration,
        val defaultHeapSize: Long
    )

    data class KafkaProps(
        val brokers: String,
        val consumingEnabled: Boolean,
        val retryEnabled: Boolean,
        val retryInterval: Long,
        val retryAttempts: Int,
        val retryTopic: String,
        val stubConsumingEnabled: Boolean,
        val receiverRetry: ReceiverRetryProps,
        val groupId: String,
        val maxPollRecords: Int,
        val offsetResetConfig: String,
        val commitInterval: Long,
        val sender: SenderProps,
        val stubTopic: String,
        val healthTimeoutMillis: Long
    )
    data class ReceiverRetryProps(
        val attempts: Long,
        val periodSeconds: Long,
        val jitter: Double,
    )

    data class SenderProps(
        val maxInFlight: Int
    )
}
