package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.stub

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent

class StubEventSerializer(private val mapper: ObjectMapper) : Serializer<StubEvent> {
    @Suppress("TooGenericExceptionCaught")
    override fun serialize(topic: String, data: StubEvent): ByteArray {
        return try {
            mapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error when serializing ConversionUpdateEvent to byte[]", e)
        }
    }
}
