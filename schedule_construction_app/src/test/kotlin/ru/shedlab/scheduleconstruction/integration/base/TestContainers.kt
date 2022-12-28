package ru.shedlab.scheduleconstruction.integration.base

import org.testcontainers.containers.PostgreSQLContainer

abstract class TestContainers {

    companion object Factory {
        private val postgresql = PostgreSQLContainer<Nothing>("postgres:14").apply {
            withReuse(true)
            withInitScript("db/setup.sql")
            start()
        }

        init {
            System.setProperty("spring.flyway.url", postgresql.jdbcUrl)
            System.setProperty("spring.flyway.user", postgresql.username)
            System.setProperty("spring.flyway.password", postgresql.password)
            System.setProperty(
                "spring.r2dbc.url",
                String.format(
                    "r2dbc:postgresql://%s:%d/%s",
                    postgresql.host,
                    postgresql.firstMappedPort,
                    postgresql.databaseName
                )
            )
            System.setProperty("spring.r2dbc.username", postgresql.username)
            System.setProperty("spring.r2dbc.password", postgresql.password)
        }
    }
}
