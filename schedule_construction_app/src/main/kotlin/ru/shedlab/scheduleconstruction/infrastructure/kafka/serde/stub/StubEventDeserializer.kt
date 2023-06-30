package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.stub

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.nio.charset.Charset

class StubEventDeserializer(
    private val mapper: ObjectMapper
) : Deserializer<StubEvent?> {
    private val log = LoggerFactory.getLogger(StubEventDeserializer::class.java)

    @Suppress("TooGenericExceptionCaught")
    override fun deserialize(topic: String, data: ByteArray): StubEvent? {
        val stringData = String(data, Charset.forName("UTF-8"))
        return try {
            mapper.readValue(stringData, StubEvent::class.java)
        } catch (e: Exception) {
            log.error("Got exception $e while trying to parse StubEvent from data $data")
            return null
        }
    }
}
