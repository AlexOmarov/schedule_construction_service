package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.dlt

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent.Companion.PayloadType
import ru.shedlab.scheduleconstruction.presentation.kafka.DltEvent.Companion.PayloadType.STUB
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.nio.charset.Charset

class DltEventDeserializer(private val mapper: ObjectMapper) : Deserializer<DltEvent<Any>> {
    private val log = LoggerFactory.getLogger(DltEventDeserializer::class.java)

    @Suppress("TooGenericExceptionCaught")
    override fun deserialize(topic: String, data: ByteArray): DltEvent<Any>? {
        val rootNode = mapper.readValue(data, JsonNode::class.java)
        val payloadType = PayloadType.valueOf(rootNode[payloadTypeNodeName].textValue())
        val stringData = rootNode[dataTypeNodeName].textValue()
        val processingAttempts = rootNode[attemptsTypeNodeName].longValue()

        return try {
            val type: JavaType = when (payloadType) {
                STUB ->
                    mapper.typeFactory.constructParametricType(
                        DltEvent::class.java,
                        StubEvent::class.java
                    )
            }
            mapper.readValue(String(data, Charset.forName("UTF-8")), type)
        } catch (e: Exception) {
            log.error("Got exception $e while trying to parse DltEvent from data $data")
            return DltEvent(
                null,
                payloadType,
                stringData,
                processingAttempts
            )
        }
    }

    companion object {
        private const val payloadTypeNodeName = "payloadType"
        private const val dataTypeNodeName = "data"
        private const val attemptsTypeNodeName = "processingAttempts"
    }
}
