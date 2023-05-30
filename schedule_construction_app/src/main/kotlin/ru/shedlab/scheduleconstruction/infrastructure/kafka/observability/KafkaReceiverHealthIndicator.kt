package ru.shedlab.scheduleconstruction.infrastructure.kafka.observability

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import reactor.core.Disposable
import reactor.core.publisher.Mono

class KafkaReceiverHealthIndicator(private val disposable: Disposable) : ReactiveHealthIndicator {
    override fun health(): Mono<Health> {
        val builder = Health.Builder()
        if (disposable.isDisposed) {
            builder.down()
        } else {
            builder.up()
        }
        return Mono.just(builder.build())
    }
}
