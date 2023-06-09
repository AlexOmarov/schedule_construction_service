package ru.shedlab.scheduleconstruction.infrastructure.config

import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianReader
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianWriter

@Configuration
class CodecConfig {
    @Bean
    fun hessianCodec(): CodecCustomizer {
        return CodecCustomizer {
            it.customCodecs().register(HessianWriter())
            it.customCodecs().register(HessianReader())
        }
    }
}
