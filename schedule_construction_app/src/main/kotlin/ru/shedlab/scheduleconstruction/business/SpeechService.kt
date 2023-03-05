package ru.shedlab.scheduleconstruction.business

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.api.web.dto.common.SpeechRequest
import ru.shedlab.scheduleconstruction.system.config.AppProps

@Service
class SpeechService(private val webClient: WebClient, private val props: AppProps) {

    fun speak(request: SpeechRequest): Mono<ByteArray> {
        return webClient.post().uri {
            it.path(props.yandex.speech.url)
                .queryParam("lang", request.lang)
                .queryParam("format", request.format)
                .queryParam("speed", request.speed)
                .queryParam("speaker", request.speaker)
                .queryParam("folderId", props.yandex.speech.folder)
                .build()
        }
            .header("Authorization", "Bearer ${props.yandex.speech.iam}")
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .bodyValue(request.text)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
    }
}