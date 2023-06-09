package ru.shedlab.scheduleconstruction.presentation.kafka

data class DltEvent<T>(
    val payload: T?,
    val key: String,
    val payloadType: PayloadType,
    val data: String,
    var processingAttempts: Int
) {
    enum class PayloadType {
        STUB
    }
}
