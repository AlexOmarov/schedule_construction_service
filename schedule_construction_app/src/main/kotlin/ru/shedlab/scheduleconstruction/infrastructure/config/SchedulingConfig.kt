package ru.shedlab.scheduleconstruction.infrastructure.config

import io.r2dbc.spi.ConnectionFactory
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.r2dbc.R2dbcLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
@EnableScheduling
@EnableSchedulerLock(
    defaultLockAtMostFor = "\${app.scheduler.max-lock}",
    defaultLockAtLeastFor = "\${app.scheduler.min-lock}"
)
@ConditionalOnProperty(name = ["app.scheduler.enabled"], havingValue = "true")
class SchedulingConfig(private val props: AppProps) : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()

        threadPoolTaskScheduler.poolSize = props.app.scheduler.threadPoolSize
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-")
        threadPoolTaskScheduler.initialize()

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
    }

    @Bean
    fun getLockProvider(connectionFactory: ConnectionFactory): LockProvider {
        return R2dbcLockProvider(connectionFactory)
    }
}
