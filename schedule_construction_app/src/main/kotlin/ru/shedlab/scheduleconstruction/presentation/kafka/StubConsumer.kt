package ru.shedlab.scheduleconstruction.presentation.kafka

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.eventhandlers.StubEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumer
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumptionResult

@Component
class StubConsumer(
    private val conversionReceiver: KafkaReceiver<String, StubEvent?>,
    private val conversionUpdateHandler: StubEventHandler,
    private val props: AppProps
) : MessageConsumer<StubEvent> {
    override suspend fun handle(event: StubEvent): MessageConsumptionResult = conversionUpdateHandler.handle(event)

    override fun getReceiver(): KafkaReceiver<String, StubEvent?> = conversionReceiver

    override fun enabled(): Boolean = props.kafka.stubConsumingEnabled

    override fun getName(): String = "STUB_RECEIVER"

    override fun getDelaySeconds() = null
}
