package ru.shedlab.scheduleconstruction.system.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "JWT", `in` = SecuritySchemeIn.HEADER)
class SwaggerConfig {

    @Bean
    fun groupedApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Schedule construction service")
                    .description("API of the SCS")
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Plans subscriptions service documentation")
                    .url("https://github.com/AlexOmarov/schedule_construction_service")
            )
    }
}
