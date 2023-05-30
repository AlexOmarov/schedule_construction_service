package ru.shedlab.scheduleconstruction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScheduleConstructionApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<ScheduleConstructionApplication>(*args)
}
