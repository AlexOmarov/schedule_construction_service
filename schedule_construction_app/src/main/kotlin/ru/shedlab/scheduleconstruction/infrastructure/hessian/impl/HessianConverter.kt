package ru.shedlab.scheduleconstruction.infrastructure.hessian.impl

import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport

class HessianConverter : HessianCodecSupport(), HttpMessageConverter<Any> {
    override fun canRead(clazz: Class<*>, mediaType: MediaType?): Boolean {
        return mediaType != null && HESSIAN_MEDIA_TYPES.contains(mediaType)
    }

    override fun canWrite(clazz: Class<*>, mediaType: MediaType?): Boolean {
        return mediaType != null && HESSIAN_MEDIA_TYPES.contains(mediaType)
    }

    override fun getSupportedMediaTypes(): MutableList<MediaType> {
        return HESSIAN_MEDIA_TYPES
    }

    override fun read(clazz: Class<out Any>, inputMessage: HttpInputMessage): Any {
        return decode(clazz, inputMessage)
    }

    override fun write(t: Any, contentType: MediaType?, outputMessage: HttpOutputMessage) {
        encode(t, outputMessage)
    }
}
