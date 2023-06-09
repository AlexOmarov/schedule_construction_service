package ru.shedlab.scheduleconstruction.unit.hessian

import com.caucho.hessian.io.HessianSerializerOutput
import org.junit.jupiter.api.Assertions
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianDecoder
import java.util.UUID

class HessianDecoderTests {

    // @Test
    fun `Decoder decodes incoming data buffer into message`() {
        val decoder = HessianDecoder()
        val buffer = DefaultDataBufferFactory().allocateBuffer(2048)
        val outStr = buffer.asOutputStream()
        val output = HessianSerializerOutput(outStr)
        val uuid = UUID.randomUUID()
        val value = "TEST"
        val message = Stub(uuid, value)
        output.startMessage()
        output.writeObject(message)
        output.completeMessage()

        outStr.close()
        output.close()
        val result = decoder.decode(Stub::class.java, buffer)
        Assertions.assertTrue(result.name == value && result.id == uuid)
    }
}
