package ru.shedlab.scheduleconstruction.infrastructure.config

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MicrometerConfig {
    @Bean
    fun customizer(props: Props, buildProps: BuildProperties): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer {
            it.config().commonTags(
                "service", buildProps.name,
                "instance", props.app.instance
            )
        }
    }
}
