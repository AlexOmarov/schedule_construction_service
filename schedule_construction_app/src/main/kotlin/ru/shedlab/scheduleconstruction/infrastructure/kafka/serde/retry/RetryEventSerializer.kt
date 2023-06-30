package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.retry

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent

class RetryEventSerializer(private val mapper: ObjectMapper) : Serializer<RetryEvent<Any>> {
    @Suppress("TooGenericExceptionCaught")
    override fun serialize(topic: String, data: RetryEvent<Any>): ByteArray {
        return try {
            mapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error when serializing RetryEvent to byte[]", e)
        }
    }
}
