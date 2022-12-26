package ru.shedlab.scheduleconstructor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScheduleConstructorApp

fun main(args: Array<String>) {
    runApplication<ScheduleConstructorApp>(*args)
}