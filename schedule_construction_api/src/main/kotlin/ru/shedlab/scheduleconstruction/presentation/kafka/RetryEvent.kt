package ru.shedlab.scheduleconstruction.presentation.kafka

data class RetryEvent<T>(
    val payload: T?,
    val key: String,
    val payloadType: PayloadType,
    val data: String,
    var attempts: Int
) {
    enum class PayloadType { STUB }
}
