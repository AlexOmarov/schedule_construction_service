package ru.shedlab.scheduleconstruction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class ScheduleConstructionApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    Hooks.enableAutomaticContextPropagation()
    runApplication<ScheduleConstructionApplication>(*args)
}
