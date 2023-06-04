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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverHealthIndicator
import java.time.Duration
import java.util.UUID

class KafkaConsumerLauncherDecorator(
    private val props: AppProps,
    private val receiverSetups: List<MessageConsumer<out Any>>,
    private val observationRegistry: ObservationRegistry
) {
    private val log = LoggerFactory.getLogger(KafkaConsumerLauncherDecorator::class.java)

    fun launchConsumers(): CompositeReactiveHealthContributor {
        val registry: MutableMap<String, ReactiveHealthIndicator> = HashMap()
        startConsumers(registry)
        return CompositeReactiveHealthContributor.fromMap(registry)
    }

    @Suppress("UNCHECKED_CAST")
    private fun startConsumers(registry: MutableMap<String, ReactiveHealthIndicator>) {
        receiverSetups.filter { it.enabled() }
            .forEach {
                val disposable = consume(it as MessageConsumer<Any>).subscribe()
                registry[it.getName()] = KafkaReceiverHealthIndicator(disposable)
            }
    }

    private fun consume(receiver: MessageConsumer<Any>): Flux<Long> {
        return getRecordsBatches(receiver)
            .concatMap { batch -> handleBatch(batch, receiver) }
            .doOnSubscribe { log.info("${receiver.getName()} receiver started") }
            .doOnTerminate { log.info("${receiver.getName()} receiver terminated") }
            .doOnError { throwable -> log.error("Got exception while processing records", throwable) }
            .retryWhen(getRetrySettings())
    }

    private fun getRecordsBatches(receiver: MessageConsumer<Any>): Flux<Flux<ConsumerRecord<String, Any?>>> {
        return Flux.defer {
            log.info("Starting ${receiver.getName()} receiver")
            var flux = receiver.getReceiver().receiveAutoAck()
            if (receiver.getDelaySeconds() != null) {
                flux = flux.delayElements(Duration.ofSeconds(props.kafka.dltHandlingInterval))
            }
            flux
        }
    }

    private fun handleBatch(records: Flux<ConsumerRecord<String, Any?>>, receiver: MessageConsumer<Any>): Mono<Long> {
        return records
            .concatMap { record -> handleRecord(record, receiver) }
            .count()
            .map { log.info("Completed batch of size $it"); it }
    }

    private fun getRetrySettings(): Retry {
        return Retry
            .backoff(props.kafka.retryAttempts, Duration.ofSeconds(props.kafka.retryPeriodSeconds))
            .jitter(props.kafka.retryJitter)
    }

    private fun handleRecord(
        record: ConsumerRecord<String, Any?>,
        receiver: MessageConsumer<Any>
    ): Mono<MessageConsumptionResult> {
        startEventObservation(record, receiver)
        return if (record.value() == null) {
            log.warn("Got empty value for record $record")
            Mono.just(MessageConsumptionResult(MessageConsumptionResult.Companion.EsbResultCode.FAILED))
        } else {
            mono(observationRegistry.asContextElement()) { receiver.handle(record.value()!!) }
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
