package ru.shedlab.scheduleconstruction.infrastructure.grpc.interceptors

import io.grpc.ForwardingServerCall
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import org.slf4j.LoggerFactory

class GrpcExceptionInterceptor : ServerInterceptor {

    private class ExceptionTranslatingServerCall<ReqT, RespT>(
        delegate: ServerCall<ReqT, RespT>
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {

        private val log = LoggerFactory.getLogger(GrpcExceptionInterceptor::class.java)

        override fun close(status: Status, trailers: io.grpc.Metadata) {
            if (status.isOk) {
                return super.close(status, trailers)
            }
            val cause = status.cause
            var newStatus = status

            log.error("Error handling gRPC endpoint: $cause, status $newStatus")

            if (status.code == Status.Code.UNKNOWN) {
                val translatedStatus = when (cause) {
                    is IllegalArgumentException -> Status.INVALID_ARGUMENT
                    is IllegalStateException -> Status.FAILED_PRECONDITION
                    else -> Status.UNKNOWN
                }
                newStatus = translatedStatus.withDescription(cause?.message).withCause(cause)
            }

            super.close(newStatus, trailers)
        }
    }

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: io.grpc.Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return next.startCall(ExceptionTranslatingServerCall(call), headers)
    }
}
