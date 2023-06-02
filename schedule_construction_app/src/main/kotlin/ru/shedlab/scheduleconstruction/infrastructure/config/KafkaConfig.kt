package ru.shedlab.scheduleconstruction.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import ru.shedlab.scheduleconstruction.infrastructure.kafka.KafkaConsumerLauncherDecorator
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumer
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaClusterHealthIndicator
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverPropagatingReceiverTracingObservationHandler
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.dlt.DltEventDeserializer
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.dlt.DltEventSerializer
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.stub.StubEventDeserializer
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.time.Duration

@Configuration
class KafkaConfig(private val props: AppProps) {
    private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @Bean
    fun stubEventReceiver(mapper: ObjectMapper): KafkaReceiver<String, StubEvent?> {
        return KafkaReceiver.create(receiverOpts(StubEventDeserializer(mapper), props.kafka.stubTopic))
    }

    @Bean
    fun dltEventReceiver(mapper: ObjectMapper): KafkaReceiver<String, DltEvent<Any>> {
        return KafkaReceiver.create(receiverOpts(DltEventDeserializer(mapper), props.kafka.dltTopic))
    }

    @Bean
    fun dltEventSender(mapper: ObjectMapper): KafkaSender<String, DltEvent<Any>> {
        return KafkaSender.create(senderProps(DltEventSerializer(mapper)))
    }

    @Bean
    fun adminClient(): AdminClient {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = props.kafka.brokers
        return AdminClient.create(configs)
    }

    @Bean
    fun kafkaReactiveReceivers(
        props: AppProps,
        receiverSetups: List<MessageConsumer<out Any>>,
        observationRegistry: ObservationRegistry
    ): CompositeReactiveHealthContributor {
        val launcher = KafkaConsumerLauncherDecorator(props, receiverSetups, observationRegistry)
        var handler = CompositeReactiveHealthContributor.fromMap(HashMap<String, ReactiveHealthIndicator>())
        if (props.kafka.consumingEnabled) {
            handler = launcher.launchConsumers()
        } else {
            logger.info("Kafka is disabled: $props")
        }
        return handler
    }

    @Bean
    fun kafkaClusterHealth(kafkaAdminClient: AdminClient): ReactiveHealthIndicator {
        return KafkaClusterHealthIndicator(kafkaAdminClient, props.kafka.healthTimeoutMillis)
    }


    @Bean
    @Order(MicrometerTracingAutoConfiguration.DEFAULT_TRACING_OBSERVATION_HANDLER_ORDER - 1)
    fun kafkaReceiverPropagatingReceiverTracingObservationHandler(tracer: Tracer, propagator: Propagator):
        KafkaReceiverPropagatingReceiverTracingObservationHandler {
        return KafkaReceiverPropagatingReceiverTracingObservationHandler(tracer, propagator)
    }

    private fun <T> receiverOpts(deserializer: Deserializer<T>, topic: String): ReceiverOptions<String, T> {
        val consumerProps = HashMap<String, Any>()
        consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = props.kafka.brokers
        consumerProps[ConsumerConfig.GROUP_ID_CONFIG] = props.kafka.groupId
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = props.kafka.maxPollRecords
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = props.kafka.offsetResetConfig

        // Here we can set custom thread scheduler (withScheduler()...)
        return ReceiverOptions.create<String, T>(consumerProps)
            .commitInterval(Duration.ofSeconds(props.kafka.commitInterval))
            .withValueDeserializer(deserializer)
            .subscription(listOf(topic))
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
}
