package ru.shedlab.scheduleconstruction.presentation.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.IMessageConsumer
import java.time.Duration

abstract class AbstractMessageConsumer<T>(private val props: Props) : IMessageConsumer<T> {

    fun buildReceiver(deserializer: Deserializer<T?>, topic: String): KafkaReceiver<String, T?> {
        val consumerProps = HashMap<String, Any>()
        consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = props.kafka.brokers
        consumerProps[ConsumerConfig.GROUP_ID_CONFIG] = props.kafka.groupId
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = props.kafka.maxPollRecords
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = props.kafka.offsetResetConfig

        // Here we can set custom thread scheduler (withScheduler()...)
        val consProps = ReceiverOptions.create<String, T>(consumerProps)
            .commitInterval(Duration.ofSeconds(props.kafka.commitInterval))
            .withValueDeserializer(deserializer)
            .subscription(listOf(topic))

        return KafkaReceiver.create(consProps)
    }
}
