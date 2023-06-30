package ru.shedlab.scheduleconstruction.presentation.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.application.eventhandlers.StubEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.IMessageConsumer
import ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.stub.StubEventDeserializer

@Component
class StubConsumer(
    mapper: ObjectMapper,
    private val handler: StubEventHandler,
    private val props: Props
) : AbstractMessageConsumer<StubEvent>(props) {
    private val receiver = buildReceiver(StubEventDeserializer(mapper), props.kafka.stubTopic)
    override suspend fun handle(event: StubEvent, metadata: EventMetadata): EventConsumptionResult =
        handler.handle(event, metadata)

    override fun getReceiver(): KafkaReceiver<String, StubEvent?> = receiver

    override fun enabled(): Boolean = props.kafka.stubConsumingEnabled

    override fun getName(): String = "STUB_RECEIVER"

    override fun getDelaySeconds() = null
    override fun getExecutionStrategy(): IMessageConsumer.ExecutionStrategy {
        return IMessageConsumer.ExecutionStrategy.SEQUENTIAL
    }
}
