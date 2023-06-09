package ru.shedlab.scheduleconstruction.infrastructure.hessian.impl

import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpOutputMessage
import org.springframework.http.codec.HttpMessageWriter
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport

class HessianWriter: HessianCodecSupport(), HttpMessageWriter<Any> {
    override fun getWritableMediaTypes(): MutableList<MediaType> {
        return HESSIAN_MEDIA_TYPES
    }

    override fun canWrite(elementType: ResolvableType, mediaType: MediaType?): Boolean {
        return mediaType != null && HESSIAN_MEDIA_TYPES.contains(mediaType)
    }

    override fun write(
        inputStream: Publisher<out Any>,
        elementType: ResolvableType,
        mediaType: MediaType?,
        message: ReactiveHttpOutputMessage,
        hints: MutableMap<String, Any>
    ): Mono<Void> {
        return Mono.from(inputStream).map { encode(it, message.bufferFactory()) }.then()
    }
}