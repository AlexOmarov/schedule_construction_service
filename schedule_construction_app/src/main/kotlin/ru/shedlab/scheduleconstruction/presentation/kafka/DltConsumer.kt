package ru.shedlab.scheduleconstruction.presentation.kafka

import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.application.eventhandlers.IEventHandler
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumer

@Component
class DltConsumer(
    private val dltReceiver: KafkaReceiver<String, DltEvent<Any>?>,
    private val dltHandler: IEventHandler<DltEvent<Any>>,
    private val props: Props
) : MessageConsumer<DltEvent<Any>> {
    override suspend fun handle(event: DltEvent<Any>, metadata: EventMetadata) = dltHandler.handle(event, metadata)

    override fun getReceiver(): KafkaReceiver<String, DltEvent<Any>?> = dltReceiver

    override fun enabled(): Boolean = props.kafka.dltEnabled

    override fun getName(): String = "DLT event receiver"

    override fun getDelaySeconds(): Long? {
        return props.kafka.dltHandlingInterval
    }
}
