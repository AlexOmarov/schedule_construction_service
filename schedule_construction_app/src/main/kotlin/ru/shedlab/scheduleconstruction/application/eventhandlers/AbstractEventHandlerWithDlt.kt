package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.KafkaSenderDecorator

abstract class AbstractEventHandlerWithDlt<T : Any>(private val sender: KafkaSenderDecorator) : IEventHandler<T> {
    private val logger = LoggerFactory.getLogger(AbstractEventHandlerWithDlt::class.java)

    @Suppress("TooGenericExceptionCaught")
    override suspend fun handle(event: T, metadata: EventMetadata): EventConsumptionResult {
        logger.info("Handle with dlt")
        return try {
            handleEvent(event, metadata)
        } catch (
            e: Exception
        ) {
            logger.error("Got exception while processing event $event", e)
            sender.sendDlt(event, metadata)
            EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED)
        }
    }

    abstract suspend fun handleEvent(event: T, metadata: EventMetadata): EventConsumptionResult
}
