package ru.shedlab.scheduleconstruction.infrastructure.kafka

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.kafka.support.micrometer.KafkaListenerObservation
import org.springframework.kafka.support.micrometer.KafkaRecordReceiverContext
import org.springframework.stereotype.Component
import reactor.core.Disposables
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverHealthIndicator
import java.time.Duration
import java.util.UUID
import javax.annotation.PreDestroy

@Component
class KafkaConsumerLauncherDecorator(
    private val props: AppProps,
    private val consumers: List<MessageConsumer<out Any>>,
    private val observationRegistry: ObservationRegistry
) {
    private val log = LoggerFactory.getLogger(KafkaConsumerLauncherDecorator::class.java)
    private val disposables = Disposables.composite()

    fun launchConsumers(): CompositeReactiveHealthContributor {
        val registry: MutableMap<String, ReactiveHealthIndicator> = HashMap()
        startConsumers(registry)
        return CompositeReactiveHealthContributor.fromMap(registry)
    }

    @PreDestroy
    private fun closeConsumers() {
        disposables.dispose()
    }

    @Suppress("UNCHECKED_CAST")
    private fun startConsumers(registry: MutableMap<String, ReactiveHealthIndicator>) {
        consumers.filter { it.enabled() }
            .forEach {
                val disposable = consume(it as MessageConsumer<Any>).subscribe()
                registry[it.getName()] = KafkaReceiverHealthIndicator(disposable)
            }
    }

    private fun consume(consumer: MessageConsumer<Any>): Flux<Long> {
        return getRecordsBatches(consumer)
            .concatMap { batch -> handleBatch(batch, consumer) }
            .doOnSubscribe { log.info("${consumer.getName()} receiver started") }
            .doOnTerminate { log.info("${consumer.getName()} receiver terminated") }
            .doOnError { throwable -> log.error("Got exception while processing records", throwable) }
            .retryWhen(getRetrySettings())
    }

    private fun getRecordsBatches(consumer: MessageConsumer<Any>): Flux<Flux<ConsumerRecord<String, Any?>>> {
        return Flux.defer {
            log.info("Starting ${consumer.getName()} consumer")
            var flux = consumer.getReceiver().receiveAutoAck()
            if (consumer.getDelaySeconds() != null) {
                flux = flux.delayElements(Duration.ofSeconds(props.kafka.dltHandlingInterval))
            }
            flux
        }
    }

    private fun handleBatch(records: Flux<ConsumerRecord<String, Any?>>, receiver: MessageConsumer<Any>): Mono<Long> {
        return records
            .groupBy { record -> record.partition() }
            .flatMap { partitionRecords ->
                if (receiver.getExecutionStrategy() == MessageConsumer.ExecutionStrategy.SEQUENTIAL) {
                    // Process records within each partition sequentially
                    partitionRecords.concatMap { record -> handleRecord(record, receiver) }
                } else {
                    // Process records within each partition parallel
                    partitionRecords.flatMap { record -> handleRecord(record, receiver) }
                }.count()
            }
            .reduce(Long::plus) // Sum the counts across all partitions
            .map { totalProcessedRecords ->
                log.info("Completed batch of size $totalProcessedRecords")
                totalProcessedRecords
            }
    }

    private fun getRetrySettings(): Retry {
        return Retry
            .backoff(props.kafka.retryAttempts, Duration.ofSeconds(props.kafka.retryPeriodSeconds))
            .jitter(props.kafka.retryJitter)
    }

    private fun handleRecord(
        record: ConsumerRecord<String, Any?>,
        receiver: MessageConsumer<Any>
    ): Mono<EventConsumptionResult> {
        startEventObservation(record, receiver)
        return if (record.value() == null) {
            log.warn("Got empty value for record $record")
            Mono.just(EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED))
        } else {
            mono(observationRegistry.asContextElement()) {
                receiver.handle(
                    record.value()!!,
                    EventMetadata(record.key() ?: "EMPTY", 0)
                )
            }
        }
    }

    private fun startEventObservation(rec: ConsumerRecord<String, Any?>, receiver: MessageConsumer<Any>) {
        val observation = KafkaListenerObservation.LISTENER_OBSERVATION.observation(
            null,
            KafkaListenerObservation.DefaultKafkaListenerObservationConvention.INSTANCE,
            { KafkaRecordReceiverContext(rec, receiver.getName()) { UUID.randomUUID().toString() } },
            observationRegistry
        )
        observation.start()
        observation.openScope()
    }
}
