package ru.shedlab.scheduleconstruction.infrastructure.hessian.impl

import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.HttpMessageDecoder
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.MimeType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport

class HessianDecoder : HessianCodecSupport(), HttpMessageDecoder<Any> {

    override fun decode(
        inputStream: Publisher<DataBuffer>,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): Flux<Any> {
        return Flux.from(inputStream).handle { buffer: DataBuffer, sink: SynchronousSink<Any> ->
            sink.next(decode(elementType.toClass(), buffer))
        }
    }

    override fun canDecode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
        return HESSIAN_MIME_TYPE == mimeType
    }

    override fun decodeToMono(
        inputStream: Publisher<DataBuffer>,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): Mono<Any> {
        return Mono.from(inputStream).map { decode(elementType.toClass(), it) }
    }

    override fun getDecodableMimeTypes(): MutableList<MimeType> {
        return HESSIAN_MIME_TYPES
    }

    override fun getDecodeHints(
        actualType: ResolvableType,
        elementType: ResolvableType,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): MutableMap<String, Any> {
        return mutableMapOf()
    }
}
