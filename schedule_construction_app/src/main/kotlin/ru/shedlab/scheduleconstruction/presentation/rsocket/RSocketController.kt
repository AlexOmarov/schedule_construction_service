package ru.shedlab.scheduleconstruction.presentation.rsocket

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import ru.shedlab.scheduleconstruction.application.StubService
import ru.shedlab.scheduleconstruction.domain.entity.Stub

@Controller
class RSocketController(val service: StubService) {
    @MessageMapping("main.{dest}")
    suspend fun main(
        @DestinationVariable dest: String,
        @Payload message: Stub,
        /*@AuthenticationPrincipal user: Jwt*/
    ): Stub? {
        return service.getStub(message.id)
    }
}