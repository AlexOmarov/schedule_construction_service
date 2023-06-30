package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.application.StubService
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult.EventConsumptionResultCode.OK
import ru.shedlab.scheduleconstruction.infrastructure.kafka.KafkaSenderDecorator
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent.PayloadType.STUB
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.util.UUID

@Service
class StubEventHandler(
    private val stubService: StubService,
    sender: KafkaSenderDecorator
) : AbstractEventHandlerWithRetry<StubEvent>(sender) {
    private val logger = LoggerFactory.getLogger(StubEventHandler::class.java)
    override suspend fun getPayloadType() = STUB

    override suspend fun handleEvent(event: StubEvent, metadata: EventMetadata): EventConsumptionResult {
        logger.info("Stub handle")
        stubService.getStub(UUID.fromString(event.id))
        return EventConsumptionResult(OK)
    }
}
