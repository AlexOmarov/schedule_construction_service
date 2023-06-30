package ru.shedlab.scheduleconstruction.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.tracing.Tracer
import io.opentelemetry.api.trace.SpanId
import io.opentelemetry.api.trace.TraceId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.retry.RetryEventSerializer
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent
import kotlin.random.Random

@Component
class KafkaSenderDecorator(
    private val mapper: ObjectMapper,
    private val tracer: Tracer,
    private val props: Props,
) {
    private val log = LoggerFactory.getLogger(KafkaSender::class.java)

    private val sender = KafkaSender.create(senderProps(RetryEventSerializer(mapper)))
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> sendRetry(
        event: T,
        metadata: EventMetadata,
        payloadType: RetryEvent.PayloadType
    ): SenderResult<RetryEvent<T>> {
        val partition = null
        val timestamp = null
        val traceParent = getTraceParentHeader()
        val headers = mutableListOf<Header>(RecordHeader(traceHeaderKey, traceParent.toByteArray()))

        val retryEvent = RetryEvent(
            event,
            metadata.key,
            payloadType,
            mapper.writeValueAsString(event),
            metadata.processingAttempts + 1
        )

        val record = ProducerRecord(
            props.kafka.retryTopic,
            partition,
            timestamp,
            metadata.key,
            retryEvent,
            headers
        )

        val result =
            sender.send(
                Flux.just(
                    SenderRecord.create(
                        record as ProducerRecord<String, RetryEvent<Any>>,
                        retryEvent
                    )
                )
            )
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
        var traceId = TraceId.fromLongs(max, min)
        var spanId = SpanId.fromLong(Random.nextLong())

        val traceContext = tracer.currentTraceContext().context()
        if (traceContext != null) {
            traceId = traceContext.traceId()
            spanId = traceContext.spanId()
        }

        return "00-$traceId-$spanId-01"
    }

    private fun <T> senderProps(serializer: Serializer<T>): SenderOptions<String, T> {
        val producerProps: MutableMap<String, Any> = HashMap()
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = props.kafka.brokers
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        return SenderOptions.create<String, T>(producerProps)
            .withValueSerializer(serializer)
            .maxInFlight(props.kafka.sender.maxInFlight)
    }

    companion object {
        const val traceHeaderKey = "traceparent"
    }
}
