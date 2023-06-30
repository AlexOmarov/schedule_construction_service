package ru.shedlab.scheduleconstruction.infrastructure.kafka.serde.retry

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent.PayloadType
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent.PayloadType.STUB
import ru.shedlab.scheduleconstruction.presentation.kafka.StubEvent
import java.nio.charset.Charset

class RetryEventDeserializer(private val mapper: ObjectMapper) : Deserializer<RetryEvent<Any>?> {
    private val log = LoggerFactory.getLogger(RetryEventDeserializer::class.java)

    @Suppress("TooGenericExceptionCaught")
    override fun deserialize(topic: String, data: ByteArray): RetryEvent<Any>? {
        val rootNode = mapper.readValue(data, JsonNode::class.java)
        val payloadType = PayloadType.valueOf(rootNode[payloadTypeNodeName].textValue())
        val stringData = rootNode[dataTypeNodeName].textValue()
        val key = rootNode[keyNodeName].textValue()
        val processingAttempts = rootNode[attemptsTypeNodeName].intValue()

        return try {
            val type: JavaType = when (payloadType) {
                STUB ->
                    mapper.typeFactory.constructParametricType(
                        RetryEvent::class.java,
                        StubEvent::class.java
                    )
            }
            mapper.readValue(String(data, Charset.forName("UTF-8")), type)
        } catch (e: Exception) {
            log.error("Got exception $e while trying to parse RetryEvent from data $data")
            return RetryEvent(
                null,
                key,
                payloadType,
                stringData,
                processingAttempts
            )
        }
    }

    companion object {
        private const val payloadTypeNodeName = "payloadType"
        private const val dataTypeNodeName = "data"
        private const val keyNodeName = "data"
        private const val attemptsTypeNodeName = "processingAttempts"
    }
}
