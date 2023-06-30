package ru.shedlab.scheduleconstruction.presentation.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.application.eventhandlers.IEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.retry.RetryEventDeserializer

@Component
class RetryConsumer(
    mapper: ObjectMapper,
    private val handler: IEventHandler<RetryEvent<Any>>,
    private val props: Props
) : AbstractMessageConsumer<RetryEvent<Any>>(props) {
    private val receiver = buildReceiver(RetryEventDeserializer(mapper), props.kafka.retryTopic)
    override suspend fun handle(event: RetryEvent<Any>, metadata: EventMetadata) = handler.handle(event, metadata)

    override fun getReceiver(): KafkaReceiver<String, RetryEvent<Any>?> = receiver

    override fun enabled(): Boolean = props.kafka.retryEnabled

    override fun getName(): String = "RETRY_CONSUMER"

    override fun getDelaySeconds(): Long? {
        return props.kafka.retryInterval
    }
}
