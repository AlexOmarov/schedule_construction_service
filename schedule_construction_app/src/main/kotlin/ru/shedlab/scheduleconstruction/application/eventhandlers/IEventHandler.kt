package ru.shedlab.scheduleconstruction.application.eventhandlers

import ru.shedlab.scheduleconstruction.infrastructure.kafka.MessageConsumptionResult

fun interface IEventHandler<T> {
    suspend fun handle(event: T): MessageConsumptionResult
}
