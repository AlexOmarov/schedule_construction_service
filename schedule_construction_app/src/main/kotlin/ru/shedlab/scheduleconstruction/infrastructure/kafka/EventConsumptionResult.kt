package ru.shedlab.scheduleconstruction.infrastructure.kafka

data class EventConsumptionResult(val code: EventConsumptionResultCode) {
    enum class EventConsumptionResultCode { OK, FAILED }
}
