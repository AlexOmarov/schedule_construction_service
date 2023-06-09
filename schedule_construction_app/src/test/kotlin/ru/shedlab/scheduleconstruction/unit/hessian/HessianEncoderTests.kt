package ru.shedlab.scheduleconstruction.unit.hessian

import com.caucho.hessian.io.HessianSerializerInput
import org.junit.jupiter.api.Assertions
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianEncoder
import java.util.UUID

class HessianEncoderTests {

    // @Test
    fun `Encoder encodes incoming data into data buffer`() {
        val encoder = HessianEncoder()
        val uuid = UUID.randomUUID()
        val value = "TEST"
        val message = Stub(uuid, value)
        val buffer = encoder.encode(message, DefaultDataBufferFactory())

        val inpStr = buffer.asInputStream()
        val hessianSerializerInput = HessianSerializerInput(inpStr)

        hessianSerializerInput.startMessage()
        val result: Stub = hessianSerializerInput.readObject() as Stub
        hessianSerializerInput.completeMessage()

        hessianSerializerInput.close()
        inpStr.close()

        DataBufferUtils.release(buffer)

        Assertions.assertTrue(result.name == value && result.id == uuid)
    }
}
