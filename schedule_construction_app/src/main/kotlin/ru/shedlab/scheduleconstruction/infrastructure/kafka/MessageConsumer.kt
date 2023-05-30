package ru.shedlab.scheduleconstruction.infrastructure.kafka

import reactor.kafka.receiver.KafkaReceiver

interface MessageConsumer<T> {
    suspend fun handle(event: T): MessageConsumptionResult
    fun getReceiver(): KafkaReceiver<String, T?>
    fun enabled(): Boolean
    fun getName(): String
    fun getDelaySeconds(): Long?
}
