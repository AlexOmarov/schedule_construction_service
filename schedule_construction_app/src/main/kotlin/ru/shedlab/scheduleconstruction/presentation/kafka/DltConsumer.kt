package ru.shedlab.scheduleconstruction.presentation.kafka

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.application.eventhandlers.IEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumer

@Component
class DltConsumer(
    private val dltReceiver: KafkaReceiver<String, DltEvent<Any>?>,
    private val dltHandler: IEventHandler<DltEvent<Any>>,
    private val props: AppProps
) : MessageConsumer<DltEvent<Any>> {
    override suspend fun handle(event: DltEvent<Any>):
        EventConsumptionResult = dltHandler.handle(event, EventMetadata(processingAttempts = 0, key = event.key))

    override fun getReceiver(): KafkaReceiver<String, DltEvent<Any>?> = dltReceiver

    override fun enabled(): Boolean = props.kafka.dltEnabled

    override fun getName(): String = "DLT event receiver"

    override fun getDelaySeconds(): Long? {
        return props.kafka.dltHandlingInterval
    }
}