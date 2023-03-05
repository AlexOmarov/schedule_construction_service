package ru.shedlab.scheduleconstruction.system.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("")
data class AppProps(val messages: MessagesProps, val yandex: YandexProps) {
    data class MessagesProps(
        val basename: String,
        val encoding: String
    )
    data class YandexProps(
        val speech: SpeechProps
    )

    data class SpeechProps(
        val iam: String,
        val folder: String,
        val url: String
    )
}
