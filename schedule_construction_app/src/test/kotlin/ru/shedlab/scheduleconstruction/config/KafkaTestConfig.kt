package ru.shedlab.scheduleconstruction.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.stub.StubEventSerializer
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent

@TestConfiguration
@AutoConfigureObservability
class KafkaTestConfig(
    private val props: Props
) {
    @Value("\${kafka.test-topic}")
    lateinit var testTopic: String

    @Bean
    fun adminClient(props: Props): AdminClient {
        val admin = AdminClient.create(
            mapOf(Pair(BOOTSTRAP_SERVERS_CONFIG, props.kafka.brokers))
        )
        admin.createTopics(
            listOf(
                NewTopic(props.kafka.stubTopic, 1, 1),
                NewTopic(props.kafka.retryTopic, 1, 1),
                NewTopic(testTopic, 3, 1)
            )
        )
        return admin
    }

    @Bean
    fun stubEventSender(mapper: ObjectMapper): KafkaSender<String, StubEvent> {
        return KafkaSender.create(senderProps(StubEventSerializer(mapper)))
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
