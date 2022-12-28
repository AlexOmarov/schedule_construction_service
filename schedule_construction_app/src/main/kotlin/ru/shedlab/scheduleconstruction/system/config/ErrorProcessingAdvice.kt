package ru.shedlab.scheduleconstruction.system.config

import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.shedlab.scheduleconstruction.api.web.standard.ResponseMetadata
import ru.shedlab.scheduleconstruction.api.web.standard.ResultCode
import ru.shedlab.scheduleconstruction.api.web.standard.StandardResponse
import java.util.*

@ControllerAdvice
class ErrorProcessingAdvice {

    private val log = LoggerFactory.getLogger(ErrorProcessingAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun handleValidationException(
        exception: Exception,
        request: ServerHttpRequest
    ): StandardResponse<String?> {
        log.error("Got exception while trying to process request ${request.uri}", exception)
        return StandardResponse(null, ResponseMetadata(ResultCode.FAILED, ""))
    }
}
