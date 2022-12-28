package ru.shedlab.scheduleconstruction.util

import org.slf4j.LoggerFactory

class MockService<T> {

    private val log = LoggerFactory.getLogger(MockService::class.java)

    fun <T> process(obj: T): T {
        log.info("MockService: got obj $obj.")
        return obj
    }
}
