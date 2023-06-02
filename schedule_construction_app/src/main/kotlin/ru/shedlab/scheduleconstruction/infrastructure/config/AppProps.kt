package ru.shedlab.scheduleconstruction.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("")
data class AppProps(
    val messages: MessagesProps,
    val app: ApplicationProps,
    val kafka: KafkaProps,
    val cache: CacheProps
) {

    data class SchedulingProps(
        val threadPoolSize: Int,
        val stubScheduler: SchedulerProps,
    )

    data class SchedulerProps(
        val batchSize: Int,
        val lockMaxDuration: Duration
    )

    data class ApplicationProps(
        val scheduler: SchedulingProps
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
        val dltEnabled: Boolean,
        val stubConsumingEnabled: Boolean,
        val retryAttempts: Long,
        val retryPeriodSeconds: Long,
        val retryJitter: Double,
        val groupId: String,
        val maxPollRecords: Int,
        val offsetResetConfig: String,
        val commitInterval: Long,
        val dltHandlingInterval: Long,
        val dltResendNumber: Int,
        val sender: SenderProps,
        val stubTopic: String,
        val healthTimeoutMillis: Long,
        val dltTopic: String
    )

    data class SenderProps(
        val maxInFlight: Int
    )
}
