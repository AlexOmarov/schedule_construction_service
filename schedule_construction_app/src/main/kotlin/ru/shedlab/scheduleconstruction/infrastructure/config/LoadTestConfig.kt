package ru.shedlab.scheduleconstruction.infrastructure.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.nio.charset.Charset

@Component
@Profile("load_test")
class LoadTestConfig(private val template: R2dbcEntityTemplate) : ApplicationListener<ApplicationReadyEvent> {
    private val logger = LoggerFactory.getLogger(LoadTestConfig::class.java)

    @Value("classpath:loadtest_migrations/migration.sql")
    lateinit var resourceFile: Resource

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Filling db for load testing started")
        val sql: String = StreamUtils.copyToString(resourceFile.inputStream, Charset.forName("UTF-8"))
        template.databaseClient.sql(sql).fetch().all().count().block()
        logger.info("Filling db for load testing finished")
    }
}
