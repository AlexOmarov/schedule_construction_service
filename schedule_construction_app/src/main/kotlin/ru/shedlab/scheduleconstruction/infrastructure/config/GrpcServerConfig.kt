package ru.shedlab.scheduleconstruction.infrastructure.config

import io.grpc.ClientInterceptor
import io.grpc.ServerInterceptor
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor
import io.micrometer.observation.ObservationRegistry
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor
import net.devh.boot.grpc.common.util.InterceptorOrder
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import ru.shedlab.scheduleconstruction.infrastructure.grpc.interceptors.GrpcExceptionInterceptor
import ru.shedlab.scheduleconstruction.infrastructure.grpc.interceptors.GrpcLoggingInterceptor
import ru.shedlab.scheduleconstruction.infrastructure.grpc.interceptors.GrpcPropagationInterceptor

@Configuration
@GrpcAdvice
class GrpcServerConfig {
    @GrpcGlobalServerInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS + interceptorOrder)
    fun serverInterceptor(): GrpcLoggingInterceptor {
        return GrpcLoggingInterceptor()
    }

    @GrpcGlobalServerInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS + propagationInterceptorOrder)
    fun serverPropagationInterceptor(observationRegistry: ObservationRegistry): GrpcPropagationInterceptor {
        return GrpcPropagationInterceptor(observationRegistry)
    }

    @GrpcGlobalClientInterceptor
    fun tracingClientInterceptor(registry: ObservationRegistry): ClientInterceptor {
        return ObservationGrpcClientInterceptor(registry)
    }

    @GrpcGlobalServerInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS + tracingInterceptorOrder)
    fun tracingServerInterceptor(registry: ObservationRegistry): ServerInterceptor {
        return ObservationGrpcServerInterceptor(registry)
    }

    @GrpcGlobalServerInterceptor
    @Order(InterceptorOrder.ORDER_TRACING_METRICS + exceptionInterceptor)
    fun serverExceptionInterceptor(): GrpcExceptionInterceptor {
        return GrpcExceptionInterceptor()
    }

    companion object {
        private const val interceptorOrder = 4
        private const val propagationInterceptorOrder = 3
        private const val tracingInterceptorOrder = 2
        private const val exceptionInterceptor = 10
    }
}
