package ru.shedlab.scheduleconstruction.presentation.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.api.web.dto.common.SpeechRequest
import ru.shedlab.scheduleconstruction.business.SpeechService
import ru.shedlab.scheduleconstruction.presentation.controller.swagger.ISwaggerSpeechController

@RestController
@RequestMapping("/alice-chatgpt")
class SpeechController(private val speechService: SpeechService) : ISwaggerSpeechController {
    @CrossOrigin(origins = ["https://chat.openai.com"], allowedHeaders = ["Content-Type"])
    @PostMapping("/speak", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    override fun speak(@RequestBody request: SpeechRequest): Mono<ByteArray> {
        return speechService.speak(request)
    }
}