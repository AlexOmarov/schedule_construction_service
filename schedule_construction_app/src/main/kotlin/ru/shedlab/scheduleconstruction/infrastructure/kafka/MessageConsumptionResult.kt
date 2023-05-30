package ru.shedlab.scheduleconstruction.infrastructure.kafka

data class MessageConsumptionResult(val code: EsbResultCode) {
    companion object {
        enum class EsbResultCode { OK, FAILED }
    }
}
