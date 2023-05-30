package ru.shedlab.scheduleconstruction.infrastructure.kafka

import io.opentelemetry.api.trace.SpanId
import io.opentelemetry.api.trace.TraceId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import kotlin.random.Random

@Component
class KafkaSenderDecorator {
    private val log = LoggerFactory.getLogger(KafkaSender::class.java)
    suspend fun <T> send(event: T, topic: String, sender: KafkaSender<String, T>, key: String?): SenderResult<T> {
        val partition = null
        val timestamp = null
        val traceParent = getTraceParentHeader()
        val headers = mutableListOf<Header>(RecordHeader(traceHeaderKey, traceParent.toByteArray()))

        val record = ProducerRecord(topic, partition, timestamp, key, event, headers)

        val result = sender.send(Flux.just(SenderRecord.create(record, event)))
            .doOnError { e -> log.error("Send failed", e) }
            .asFlow()
            .first()

        log.info(
            "Message has been sent: ${result.correlationMetadata()} " +
                "traceParent: $traceParent, " +
                "topic/partition: ${result.recordMetadata().topic()}/${result.recordMetadata().partition()}, " +
                "offset: ${result.recordMetadata().offset()}, " +
                "timestamp: ${result.recordMetadata().timestamp()}"
        )

        return result
    }

    private fun getTraceParentHeader(): String {
        val min = Random.nextLong(2L, Long.MAX_VALUE - 2)
        val max = Random.nextLong(min, Long.MAX_VALUE)
        val traceId = TraceId.fromLongs(max, min)
        val spanId = SpanId.fromLong(Random.nextLong())
        return "00-$traceId-$spanId-01"
    }

    companion object {
        const val traceHeaderKey = "traceparent"
    }
}
