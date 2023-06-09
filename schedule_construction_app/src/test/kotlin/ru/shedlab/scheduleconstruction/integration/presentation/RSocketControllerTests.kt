package ru.shedlab.scheduleconstruction.integration.presentation

import io.rsocket.core.RSocketConnector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.MimeType
import reactor.util.retry.Retry
import ru.shedlab.scheduleconstruction.application.StubService
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianDecoder
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianEncoder
import java.net.URI
import java.time.Duration
import java.util.*
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebFluxTest
class RSocketControllerTests {

    @MockBean
    private lateinit var service: StubService

    private val log = LoggerFactory.getLogger(javaClass)

    @Test
    fun `test that messages API returns latest messages`() {
        val rSocketRequester = RSocketRequester.builder()
            .rsocketConnector { rSocketConnector: RSocketConnector ->
                rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
            }
            .dataMimeType(MimeType("application", "x-hessian"))
            .rsocketStrategies(
                RSocketStrategies.builder()
                    .encoders { it.add(HessianEncoder()) }
                    .decoders { it.add(HessianDecoder()) }
                    .build())
            .websocket(URI.create("http://localhost:7000/rsocket"))


        rSocketRequester
            .route("main.${Random.nextInt()}")
            .data(Stub(UUID.randomUUID(), "Rsocket request"))
            .retrieveFlux(Stub::class.java)
            .doOnNext {
                log.info(it.name)
            }
            .subscribe()
    }
}