package ru.shedlab.scheduleconstruction.infrastructure.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class FlywayConfig(private val env: Environment) {
    @Bean(initMethod = "migrate")
    fun flyway() = Flyway(
        Flyway.configure().baselineOnMigrate(true).dataSource(
            env.getRequiredProperty("spring.flyway.url"),
            env.getRequiredProperty("spring.flyway.user"),
            env.getRequiredProperty("spring.flyway.password")
        ).defaultSchema(env.getRequiredProperty("spring.flyway.default-schema"))
    )
}
