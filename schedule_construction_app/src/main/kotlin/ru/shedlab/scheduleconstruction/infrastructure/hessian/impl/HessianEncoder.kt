package ru.shedlab.scheduleconstruction.infrastructure.hessian.impl

import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageEncoder
import org.springframework.util.MimeType
import reactor.core.publisher.Flux
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport


class HessianEncoder : HessianCodecSupport(), HttpMessageEncoder<Any> {

    override fun getStreamingMediaTypes(): List<MediaType> {
        return HESSIAN_MEDIA_TYPES
    }

    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
        return HESSIAN_MIME_TYPE == mimeType
    }

    override fun encode(
        inputStream: Publisher<out Any>,
        bufferFactory: DataBufferFactory,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): Flux<DataBuffer> {
        return Flux.from(inputStream).map { encode(it, bufferFactory) }
    }

    override fun encodeValue(
        value: Any,
        bufferFactory: DataBufferFactory,
        valueType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): DataBuffer {
        return encode(value, bufferFactory)
    }

    override fun getEncodableMimeTypes(): List<MimeType> {
        return HESSIAN_MIME_TYPES
    }
}