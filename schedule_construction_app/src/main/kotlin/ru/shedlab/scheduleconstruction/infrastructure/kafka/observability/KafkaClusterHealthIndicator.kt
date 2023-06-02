package ru.shedlab.scheduleconstruction.infrastructure.kafka.observability

import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.KafkaFuture
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KafkaClusterHealthIndicator(
    private val kafkaAdminClient: AdminClient,
    private val timeoutMillis: Long
) : ReactiveHealthIndicator {
    override fun health(): Mono<Health> {
        return mono { checkKafkaHealth() }
            .timeout(Duration.ofMillis(timeoutMillis))
            .onErrorResume { Mono.just(Health.down(IllegalStateException("Kafka unavailable")).build()) }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun checkKafkaHealth(): Health {
        return try {
            kafkaAdminClient.describeCluster().clusterId().await()
            Health.up().build()
        } catch (ex: Exception) {
            Health.down(ex).build()
        }
    }

    private suspend fun <T> KafkaFuture<T>.await(): T = suspendCoroutine { continuation ->
        this.whenComplete { result, exception ->
            if (exception != null) {
                continuation.resumeWithException(exception)
            } else {
                continuation.resume(result)
            }
        }
    }
}
