package ru.shedlab.scheduleconstruction.config

import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.presentation.grpc.StubRequest
import ru.shedlab.scheduleconstruction.presentation.grpc.StubResponse
import ru.shedlab.scheduleconstruction.presentation.grpc.StubServiceGrpcKt

@Service
class GrpcTestClient {
    @GrpcClient("scheduler_construction_service")
    private lateinit var client: StubServiceGrpcKt.StubServiceCoroutineStub

    suspend fun getStub(request: StubRequest): StubResponse {
        return client.getStub(request)
    }
}
