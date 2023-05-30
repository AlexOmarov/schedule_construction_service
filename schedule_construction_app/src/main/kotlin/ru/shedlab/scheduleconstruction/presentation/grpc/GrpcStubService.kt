package ru.shedlab.scheduleconstruction.presentation.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import ru.shedlab.scheduleconstruction.application.StubService
import java.util.*

@GrpcService
class GrpcStubService(private val stubService: StubService) : StubServiceGrpcKt.StubServiceCoroutineImplBase() {

    private val logger = LoggerFactory.getLogger(GrpcStubService::class.java)

    override suspend fun getStub(request: StubRequest): StubResponse {
        logger.info("Getting getConversion method with request $request")
        stubService.getStub(UUID.fromString(request.id))
        return super.getStub(request)
    }
}
