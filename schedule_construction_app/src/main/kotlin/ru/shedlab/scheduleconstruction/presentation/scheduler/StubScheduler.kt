package ru.shedlab.scheduleconstruction.presentation.scheduler

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.rsocket.metadata.WellKnownMimeType
import kotlinx.coroutines.runBlocking
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.MimeTypeUtils
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import java.util.*
import kotlin.random.Random

@Component
@ConditionalOnExpression("\${app.scheduler.stub-scheduler.enabled} and \${app.scheduler.enabled}")
class StubScheduler(
    private val observationRegistry: ObservationRegistry,
    private val rSocketRequester: RSocketRequester
) {

    private val log = LoggerFactory.getLogger(StubScheduler::class.java)

    @SchedulerLock(
        name = "RequestSpyScheduler",
        lockAtMostFor = "\${app.scheduler.stub-scheduler.lock-max-duration}"
    )
    @Scheduled(fixedDelayString = "\${app.scheduler.stub-scheduler.delay}", zone = "UTC")
    fun launch() {
        val observation = Observation.start("StubScheduler", observationRegistry)
        observation.observe {
            runBlocking {
                log.info("Started StubScheduler")
                val stub = rSocketRequester
                    .route("main.${Random.nextInt()}")
                    .metadata("", RSOCKET_AUTHENTICATION_MIME_TYPE)
                    .data(Stub(UUID.randomUUID(), "Rsocket request"))
                    .retrieveAndAwaitOrNull<Stub>()
                log.info("Received $stub from scheduler")
                log.info("StubScheduler has been completed")
            }
        }
    }

    companion object {
        val RSOCKET_AUTHENTICATION_MIME_TYPE =
            MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
    }
}
