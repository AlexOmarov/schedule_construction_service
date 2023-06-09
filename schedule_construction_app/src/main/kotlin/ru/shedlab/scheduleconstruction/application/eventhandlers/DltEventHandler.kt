package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent

@Service
class DltEventHandler(
    private val props: AppProps,
    private val stubHandler: IEventHandler<StubEvent>
) : IEventHandler<DltEvent<Any>> {
    override suspend fun handle(event: DltEvent<Any>, metadata: EventMetadata): EventConsumptionResult {
        val result = if (event.payload == null || props.kafka.dltResendNumber <= event.processingAttempts) {
            EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED)
        } else {
            when (event.payloadType) {
                DltEvent.PayloadType.STUB -> stubHandler.handle(
                    event.payload as StubEvent,
                    EventMetadata(event.key, event.processingAttempts)
                )
            }
        }
        return result
    }
}
