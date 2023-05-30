package ru.shedlab.scheduleconstruction.presentation.kafka

data class DltEvent<T>(
    val payload: T?,
    val payloadType: PayloadType,
    val data: String,
    var processingAttempts: Long
) {
    companion object {
        enum class PayloadType {
            STUB
        }
    }
}
