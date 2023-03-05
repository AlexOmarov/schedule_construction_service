package ru.shedlab.scheduleconstruction.presentation.controller.swagger

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.api.web.dto.common.SpeechRequest

interface ISwaggerSpeechController {
    fun speak(request: SpeechRequest): Mono<ByteArray>
}