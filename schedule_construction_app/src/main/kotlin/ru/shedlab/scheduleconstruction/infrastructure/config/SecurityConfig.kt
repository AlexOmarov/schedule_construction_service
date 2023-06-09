package ru.shedlab.scheduleconstruction.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.SecureRandom


@Configuration
class SecurityConfig(val props: AppProps) {

    private val corsPattern = "/**"
    private val authnPattern = "/**"

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(11, SecureRandom())
    }

    /*@Bean
    fun securityWebFilterChain(
        http: SecurityConfig,
        authenticationManagerResolver: ReactiveAuthenticationManagerResolver<ServerWebExchange>
    ): SecurityWebFilterChain {
        return http
            // CSRF
            .csrf().disable()
            // CORS
            .cors().configurationSource(
                UrlBasedCorsConfigurationSource(PathPatternParser()).also { source ->
                    source.setCorsConfigurations(
                        Collections.singletonMap(corsPattern, CorsConfiguration().also {
                            val cors = props.security.cors
                            it.allowedOrigins = cors.origins
                            it.allowedMethods = cors.methods
                            it.allowedHeaders = cors.headers
                            it.exposedHeaders = cors.exposedHeaders
                            it.allowCredentials = cors.allowCreds
                            it.maxAge = cors.age
                        })
                    )
                }
            ).and()
            // Authn
            .httpBasic().disable()
            .formLogin().disable().oauth2ResourceServer().authenticationManagerResolver(authenticationManagerResolver)
            .bearerTokenConverter(authenticationConverter()).and()
            .logout().and()
            // Authz
            .authorizeExchange()
            .pathMatchers(*props.security.open.map { it }.toTypedArray()).hasRole("PLAYER")
            .pathMatchers(authnPattern).authenticated()
            .and().build()
    }*/

/*    @Bean
    fun reactiveAuthenticationManagerAdapter(authenticationManager: AuthenticationManager): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManagerAdapter(authenticationManager)
    }

    @Bean
    fun resolver(authenticationManager: ReactiveAuthenticationManager): ReactiveAuthenticationManagerResolver<ServerWebExchange> {
        return ReactiveAuthenticationManagerResolver { Mono.just(authenticationManager) }
    }*/

/*    @Bean
    fun authenticationManager(providers: List<AuthenticationProvider>): AuthenticationManager {
        return ProviderManager(providers)
    }*/

    /*@Bean
    fun authenticationConverter(): ServerAuthenticationConverter {
        return ServerBearerTokenAuthenticationConverter()
    }

    // Only provider used in filters
    @Bean
    fun jwtProvider(
        authoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>>,
        validators: List<OAuth2TokenValidator<Jwt>>
    ): JwtAuthenticationProvider {
        val processor = DefaultJWTProcessor<SecurityContext>()
        processor.jwsKeySelector = SingleKeyJWSKeySelector(JWSAlgorithm.RS512, props.security.jwt.keys.public)
        val decoder = NimbusJwtDecoder(processor)
        val provider = JwtAuthenticationProvider(decoder)
        val converter = JwtAuthenticationConverter()
        decoder.setJwtValidator(DelegatingOAuth2TokenValidator(validators))
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter)
        provider.setJwtAuthenticationConverter(converter)
        return provider
    }

    @Bean
    fun authoritiesConverter(): JwtGrantedAuthoritiesConverter {
        return JwtGrantedAuthoritiesConverter().also { it.setAuthorityPrefix("ROLE_") }
    }

    // Provider used in login endpoint
    @Bean
    fun daoProvider(persistenceFacade: PersistenceFacade): DaoAuthenticationProvider {
        return DaoAuthenticationProvider().also { it.setUserDetailsService(DefaultUserDetailsService(persistenceFacade)) }
    }*/

}