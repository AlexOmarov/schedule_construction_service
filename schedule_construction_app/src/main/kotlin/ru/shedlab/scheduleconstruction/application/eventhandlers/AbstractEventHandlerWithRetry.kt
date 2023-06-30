package ru.shedlab.scheduleconstruction.application.eventhandlers

import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult
import ru.shedlab.scheduleconstruction.infrastructure.kafka.KafkaSenderDecorator
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent

abstract class AbstractEventHandlerWithRetry<T : Any>(private val sender: KafkaSenderDecorator) : IEventHandler<T> {
    private val logger = LoggerFactory.getLogger(AbstractEventHandlerWithRetry::class.java)

    @Suppress("TooGenericExceptionCaught")
    override suspend fun handle(event: T, metadata: EventMetadata): EventConsumptionResult {
        logger.info("Handle with retry")
        return try {
            handleEvent(event, metadata)
        } catch (
            e: Exception
        ) {
            logger.error("Got exception while processing event $event", e)
            sender.sendRetry(event, metadata, getPayloadType())
            EventConsumptionResult(EventConsumptionResult.EventConsumptionResultCode.FAILED)
        }
    }

    abstract suspend fun handleEvent(event: T, metadata: EventMetadata): EventConsumptionResult

    abstract suspend fun getPayloadType(): RetryEvent.PayloadType
}
