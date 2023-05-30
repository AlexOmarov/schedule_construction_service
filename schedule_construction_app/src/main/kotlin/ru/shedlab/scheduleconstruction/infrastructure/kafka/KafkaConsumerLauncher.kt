package ru.shedlab.scheduleconstruction.infrastructure.kafka

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.micrometer.KafkaListenerObservation
import org.springframework.kafka.support.micrometer.KafkaRecordReceiverContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverHealthIndicator
import java.time.Duration
import java.util.UUID

@Configuration
class KafkaConsumerLauncher(
    private val props: AppProps,
    private val receiverSetups: List<MessageConsumer<out Any>>,
    private val observationRegistry: ObservationRegistry
) {
    private val log = LoggerFactory.getLogger(KafkaConsumerLauncher::class.java)

    @Bean
    @Suppress("UNCHECKED_CAST")
    fun kafkaReactiveReceivers(): CompositeReactiveHealthContributor {
        val registry: MutableMap<String, ReactiveHealthIndicator> = HashMap()
        if (props.kafka.consumingEnabled) {
            receiverSetups.filter { it.enabled() }
                .forEach { setup ->
                    registry["receiver ${setup.getName()}"] = KafkaReceiverHealthIndicator(
                        getRecordsFlux(setup as MessageConsumer<Any>).subscribe()
                    )
                }
        } else {
            log.info("Kafka is disabled: $props")
        }
        return CompositeReactiveHealthContributor.fromMap(registry)
    }

    private fun getRecordsFlux(receiver: MessageConsumer<Any>): Flux<Long> {
        return Flux.defer {
            log.info("Starting ${receiver.getName()}...")
            var flux = receiver.getReceiver().receiveAutoAck()
            if (receiver.getDelaySeconds() != null) {
                flux = flux.delayElements(Duration.ofSeconds(props.kafka.dltHandlingInterval))
            }
            flux
        }
            .concatMap { records -> handleBatchOfRecordsInParallel(records, receiver) }
            .doOnError { throwable -> log.error("Got exception while processing records", throwable) }
            .doOnTerminate { log.error("${receiver.getName()} receiver terminated") }
            .retryWhen(getRetrySettings())
            .doOnSubscribe { log.info("${receiver.getName()} started") }
    }

    private fun handleBatchOfRecordsInParallel(
        records: Flux<ConsumerRecord<String, Any?>>,
        receiver: MessageConsumer<Any>
    ): Mono<Long> {
        return records
            .concatMap { record -> setParentTraceAndProcessRecord(record, receiver) }
            .count()
            .map { log.info("Completed batch of size $it"); it }
    }

    private fun setParentTraceAndProcessRecord(
        record: ConsumerRecord<String, Any?>,
        receiver: MessageConsumer<Any>
    ): Mono<MessageConsumptionResult> {
        return Mono.just(record)
            .name("event")
            .flatMap { rec ->
                val observation = KafkaListenerObservation.LISTENER_OBSERVATION.observation(
                    null,
                    KafkaListenerObservation.DefaultKafkaListenerObservationConvention.INSTANCE,
                    { KafkaRecordReceiverContext(rec, receiver.getName()) { UUID.randomUUID().toString() } },
                    observationRegistry
                )
                observation.start()
                observation.openScope()
                if (record.value() == null) {
                    log.warn("Got empty value for record $record")
                    Mono.just(MessageConsumptionResult(MessageConsumptionResult.Companion.EsbResultCode.FAILED))
                } else {
                    mono(observationRegistry.asContextElement()) {
                        receiver.handle(record.value()!!)
                    }
                }
            }
    }

    private fun getRetrySettings(): Retry {
        return Retry
            .backoff(props.kafka.retryAttempts, Duration.ofSeconds(props.kafka.retryPeriodSeconds))
            .jitter(props.kafka.retryJitter)
    }
}
