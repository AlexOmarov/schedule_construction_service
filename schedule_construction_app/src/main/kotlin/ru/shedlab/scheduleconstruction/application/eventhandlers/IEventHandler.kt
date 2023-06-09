package ru.shedlab.scheduleconstruction.application.eventhandlers

import ru.shedlab.scheduleconstruction.application.dto.EventMetadata
import ru.shedlab.scheduleconstruction.infrastructure.kafka.EventConsumptionResult

fun interface IEventHandler<T> {
    suspend fun handle(event: T, metadata: EventMetadata): EventConsumptionResult
}
