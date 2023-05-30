package ru.shedlab.scheduleconstruction.presentation.scheduler

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.runBlocking
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.shedlab.scheduleconstruction.application.StubService
import java.util.*

@Component
@ConditionalOnExpression("\${app.scheduler.stub-scheduler.enabled} and \${app.scheduler.enabled}")
class StubScheduler(
    private val observationRegistry: ObservationRegistry,
    private val stubService: StubService
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
                stubService.getStub(UUID.fromString("3a20e261-d239-408c-a273-6a27081f0068"))
                log.info("StubScheduler has been completed")
            }
        }
    }
}
