package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.application.StubService
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumptionResult.MessageConsumptionResultCode.OK
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.util.*

@Service
class StubEventHandler(private val stubService: StubService) : IEventHandler<StubEvent> {
    private val logger = LoggerFactory.getLogger(StubEventHandler::class.java)
    override suspend fun handle(event: StubEvent): MessageConsumptionResult {
        logger.info("TODO")
        stubService.getStub(UUID.fromString(event.id))
        return MessageConsumptionResult(OK)
    }
}
