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
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverHealthIndicator
import java.time.Duration
import java.util.UUID
import java.util.function.Supplier
import javax.annotation.PreDestroy

@Component
class KafkaConsumerLauncherDecorator(
    private val props: Props,
    private val consumers: List<IMessageConsumer<out Any>>,
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
                val disposable = consume(it as IMessageConsumer<Any>).subscribe()
                registry[it.getName()] = KafkaReceiverHealthIndicator(disposable)
            }
    }

    private fun consume(consumer: IMessageConsumer<Any>): Flux<Long> {
        return getRecordsBatches(consumer)
            .concatMap { batch -> handleBatch(batch, consumer) }
            .doOnSubscribe { log.info("${consumer.getName()} receiver started") }
            .doOnTerminate { log.info("${consumer.getName()} receiver terminated") }
            .doOnError { throwable -> log.error("Got exception while processing records", throwable) }
            .retryWhen(getRetrySettings())
    }

    private fun getRecordsBatches(consumer: IMessageConsumer<Any>): Flux<Flux<ConsumerRecord<String, Any?>>> {
        return Flux.defer {
            log.info("Starting ${consumer.getName()} consumer")
            var flux = consumer.getReceiver().receiveAutoAck()
            if (consumer.getDelaySeconds() != null) {
                flux = flux.delayElements(Duration.ofSeconds(props.kafka.retryInterval))
            }
            flux
        }
    }

    private fun handleBatch(records: Flux<ConsumerRecord<String, Any?>>, receiver: IMessageConsumer<Any>): Mono<Long> {
        return records
            .groupBy { record -> record.partition() }
            .flatMap { partitionRecords ->
                if (receiver.getExecutionStrategy() == IMessageConsumer.ExecutionStrategy.SEQUENTIAL) {
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
            .backoff(props.kafka.receiverRetry.attempts, Duration.ofSeconds(props.kafka.receiverRetry.periodSeconds))
            .jitter(props.kafka.receiverRetry.jitter)
    }

    private fun handleRecord(
        record: ConsumerRecord<String, Any?>,
        receiver: IMessageConsumer<Any>
    ): Mono<EventConsumptionResult> {
        val observation = KafkaListenerObservation.LISTENER_OBSERVATION.observation(
            null,
            KafkaListenerObservation.DefaultKafkaListenerObservationConvention.INSTANCE,
            { KafkaRecordReceiverContext(record, receiver.getName()) { UUID.randomUUID().toString() } },
            observationRegistry
        )

        return observation.observe(
            Supplier {
                return@Supplier if (record.value() == null) {
                    log.warn("Got empty value for record $record")
                    Mono.just(EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED))
                } else {
                    mono(observationRegistry.asContextElement()) {
                        receiver.handle(record.value()!!, EventMetadata(record.key() ?: "EMPTY_KEY", zeroAttempts))
                    }
                }
            }
        )!!
    }

    companion object {
        const val emptyKey = "EMPTY_KEY"
        const val zeroAttempts = 0
    }
}
