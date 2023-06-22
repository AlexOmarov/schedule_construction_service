package ru.shedlab.scheduleconstruction.infrastructure.kafka

import reactor.kafka.receiver.KafkaReceiver
import ru.shedlab.scheduleconstruction.application.dto.EventMetadata

interface MessageConsumer<T> {
    suspend fun handle(event: T, metadata: EventMetadata): EventConsumptionResult
    fun getReceiver(): KafkaReceiver<String, T?>
    fun enabled(): Boolean
    fun getName(): String
    fun getDelaySeconds(): Long?
    fun getExecutionStrategy(): ExecutionStrategy = ExecutionStrategy.PARALLEL

    enum class ExecutionStrategy {
        PARALLEL, SEQUENTIAL
    }
}
