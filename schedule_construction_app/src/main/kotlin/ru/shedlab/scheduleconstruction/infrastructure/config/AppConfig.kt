package ru.shedlab.scheduleconstruction.infrastructure.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan
class AppConfig(private val registry: MeterRegistry, private val buildProps: BuildProperties) {
    private val log = LoggerFactory.getLogger(AppConfig::class.java)

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Gauge.builder("project_version") { 1 }
            .description("Version of project in tag")
            .tag("version", buildProps.version)
            .register(registry)
    }

    @Bean
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        return ObservedAspect(observationRegistry)
    }

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val mapper = JsonMapper.builder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build()
            .registerModule(
                KotlinModule.Builder()
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
            .registerModule(JavaTimeModule())

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

        return mapper
    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.filters { exchangeFilterFunctions ->
            exchangeFilterFunctions.add(
                ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
                    clientRequest.headers()
                        .forEach { name, values -> values.forEach { value -> log.info("{}={}", name, value) } }
                    Mono.just(clientRequest)
                }
            )
        }.build()
    }
}
