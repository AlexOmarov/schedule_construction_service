package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.infrastructure.config.AppProps
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumptionResult
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent

@Service
class DltEventHandler(
    private val props: AppProps,
    private val stubHandler: IEventHandler<StubEvent>
) : IEventHandler<DltEvent<Any>> {
    override suspend fun handle(event: DltEvent<Any>): MessageConsumptionResult {
        val result = if (event.payload == null || props.kafka.dltResendNumber <= event.processingAttempts) {
            MessageConsumptionResult(MessageConsumptionResult.MessageConsumptionResultCode.FAILED)
        } else {
            when (event.payloadType) {
                DltEvent.PayloadType.STUB -> stubHandler.handle(event.payload as StubEvent)
            }
        }
        return result
    }
}
