package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.dlt

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent

class DltEventSerializer(private val mapper: ObjectMapper) : Serializer<DltEvent<Any>> {
    @Suppress("TooGenericExceptionCaught")
    override fun serialize(topic: String, data: DltEvent<Any>): ByteArray {
        return try {
            mapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw SerializationException("Error when serializing ConversionUpdateDltEvent to byte[]", e)
        }
    }
}
