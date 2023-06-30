package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent

@Service
class RetryEventHandler(
    private val props: Props,
    private val stubHandler: IEventHandler<StubEvent>
) : IEventHandler<RetryEvent<Any>> {
    override suspend fun handle(event: RetryEvent<Any>, metadata: EventMetadata): EventConsumptionResult {
        val result = if (event.payload == null || props.kafka.retryAttempts <= event.attempts) {
            EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED)
        } else {
            when (event.payloadType) {
                RetryEvent.PayloadType.STUB -> stubHandler.handle(
                    event.payload as StubEvent,
                    EventMetadata(event.key, event.attempts)
                )
            }
        }
        return result
    }
}
