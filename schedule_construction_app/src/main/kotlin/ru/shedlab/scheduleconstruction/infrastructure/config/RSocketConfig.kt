package ru.shedlab.scheduleconstruction.infrastructure.config

import io.rsocket.core.RSocketConnector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import reactor.util.retry.Retry
import ru.shedlab.scheduleconstruction.infrastructure.hessian.HessianCodecSupport.Companion.HESSIAN_MIME_TYPE
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianDecoder
import ru.shedlab.scheduleconstruction.infrastructure.hessian.impl.HessianEncoder
import java.net.URI
import java.time.Duration

@Configuration
class RSocketConfig(private val props: AppProps) {

    @Bean
    fun messageHandler(): RSocketMessageHandler {
        val handler = RSocketMessageHandler()
        handler.rSocketStrategies = RSocketStrategies.builder()
            .encoders { it.add(HessianEncoder())/*; it.add(BearerTokenAuthenticationEncoder())*/ }
            .decoders { it.add(HessianDecoder()) }
            .build()
        /*handler.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())*/
        return handler
    }

    /*    @Bean
        fun authorization(
            security: RSocketSecurity,
            authenticationManager: ReactiveAuthenticationManager
        ): PayloadSocketAcceptorInterceptor {
            security.authorizePayload { authorize: AuthorizePayloadsSpec ->
                authorize
                    .anyRequest().hasRole("PLAYER")
                    .anyExchange().permitAll()
            }.jwt { it.authenticationManager(authenticationManager) }
            return security.build()
        }*/

    @Bean
    fun rSocketRequester(): RSocketRequester {
        val builder = RSocketRequester.builder()
        return builder
            .rsocketConnector { rSocketConnector: RSocketConnector ->
                rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
            }
            .dataMimeType(HESSIAN_MIME_TYPE)
            .rsocketStrategies(RSocketStrategies.builder()
                .encoders { it.add(HessianEncoder())/*; it.add(BearerTokenAuthenticationEncoder())*/ }
                .decoders { it.add(HessianDecoder()) }
                .build())
            .websocket(URI.create(props.rsocket.uri))
    }
}