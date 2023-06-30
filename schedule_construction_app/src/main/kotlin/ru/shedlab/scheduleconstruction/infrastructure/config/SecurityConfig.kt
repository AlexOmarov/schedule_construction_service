package ru.shedlab.scheduleconstruction.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom

@Configuration
class SecurityConfig(val props: Props) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(encoderStrength, SecureRandom())
    }

    companion object {
        private const val encoderStrength = 11
    }
}
