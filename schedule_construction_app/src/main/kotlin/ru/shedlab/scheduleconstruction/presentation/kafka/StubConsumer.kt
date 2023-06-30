package ru.shedlab.scheduleconstruction.presentation.kafka

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.application.eventhandlers.StubEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumer

@Component
class StubConsumer(
    private val conversionReceiver: KafkaReceiver<String, StubEvent?>,
    private val conversionUpdateHandler: StubEventHandler,
    private val props: Props
) : MessageConsumer<StubEvent> {
    override suspend fun handle(event: StubEvent, metadata: EventMetadata): EventConsumptionResult =
        conversionUpdateHandler.handle(event, metadata)

    override fun getReceiver(): KafkaReceiver<String, StubEvent?> = conversionReceiver

    override fun enabled(): Boolean = props.kafka.stubConsumingEnabled

    override fun getName(): String = "STUB_RECEIVER"

    override fun getDelaySeconds() = null
    override fun getExecutionStrategy(): MessageConsumer.ExecutionStrategy {
        return MessageConsumer.ExecutionStrategy.SEQUENTIAL
    }
}
