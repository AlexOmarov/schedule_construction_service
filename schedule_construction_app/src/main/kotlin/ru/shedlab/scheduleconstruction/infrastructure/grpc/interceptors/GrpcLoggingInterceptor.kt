package ru.shedlab.scheduleconstruction.infrastructure.grpc.interceptors

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GrpcLoggingInterceptor : ServerInterceptor {

    private val log: Logger = LoggerFactory.getLogger(GrpcLoggingInterceptor::class.java)

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        // Add parent observation for all the call
        val listener: ServerCall<ReqT, RespT> = object : SimpleForwardingServerCall<ReqT, RespT>(call) {
            override fun sendMessage(message: RespT) {
                log.info("Sending message to clients: {}", message)
                super.sendMessage(message)
            }
        }

        return object : SimpleForwardingServerCallListener<ReqT>(next.startCall(listener, headers)) {
            override fun onMessage(message: ReqT) {
                log.info("Received message from client: {}", message)
                super.onMessage(message)
            }
        }
    }
}
