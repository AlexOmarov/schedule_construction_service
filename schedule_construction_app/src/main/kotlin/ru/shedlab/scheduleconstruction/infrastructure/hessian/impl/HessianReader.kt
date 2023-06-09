package ru.shedlab.scheduleconstruction.infrastructure.hessian.impl

import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpInputMessage
import org.springframework.http.codec.HttpMessageReader
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport

class HessianReader: HessianCodecSupport(), HttpMessageReader<Any> {
    override fun getReadableMediaTypes(): MutableList<MediaType> {
        return HESSIAN_MEDIA_TYPES
    }

    override fun canRead(elementType: ResolvableType, mediaType: MediaType?): Boolean {
        return mediaType != null && HESSIAN_MEDIA_TYPES.contains(mediaType)
    }

    override fun read(
        elementType: ResolvableType,
        message: ReactiveHttpInputMessage,
        hints: MutableMap<String, Any>
    ): Flux<Any> {
        return message.body.map { decode(elementType.toClass(), it) }
    }

    override fun readMono(
        elementType: ResolvableType,
        message: ReactiveHttpInputMessage,
        hints: MutableMap<String, Any>
    ): Mono<Any> {
        return message.body.next().map { decode(elementType.toClass(), it) }
    }
}