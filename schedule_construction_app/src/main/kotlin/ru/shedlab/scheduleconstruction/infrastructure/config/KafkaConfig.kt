package ru.shedlab.scheduleconstruction.infrastructure.config

import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import ru.shedlab.scheduleconstruction.infrastructure.kafka.KafkaConsumerLauncherDecorator
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaClusterHealthIndicator
import ru.shedlab.scheduleconstruction.infrastructure.kafka.observability.KafkaReceiverPropagatingTracingObservationHandler

@Configuration
class KafkaConfig(private val props: Props) {
    private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @Bean
    fun adminClient(): AdminClient {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = props.kafka.brokers
        return AdminClient.create(configs)
    }

    @Bean
    fun kafkaReactiveReceivers(decorator: KafkaConsumerLauncherDecorator): CompositeReactiveHealthContributor {
        var handler = CompositeReactiveHealthContributor.fromMap(HashMap<String, ReactiveHealthIndicator>())
        if (props.kafka.consumingEnabled) {
            handler = decorator.launchConsumers()
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
    fun kafkaReceiverPropagatingTracingObservationHandler(tracer: Tracer, propagator: Propagator):
        KafkaReceiverPropagatingTracingObservationHandler {
        return KafkaReceiverPropagatingTracingObservationHandler(tracer, propagator)
    }
}
