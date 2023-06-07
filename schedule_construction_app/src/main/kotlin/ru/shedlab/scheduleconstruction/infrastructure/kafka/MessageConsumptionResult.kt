package ru.shedlab.scheduleconstruction.infrastructure.kafka

data class MessageConsumptionResult(val code: MessageConsumptionResultCode) {
    enum class MessageConsumptionResultCode { OK, FAILED }
}
