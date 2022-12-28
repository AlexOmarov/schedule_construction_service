package ru.shedlab.scheduleconstruction.system.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("")
data class AppProps(val messages: MessagesProps) {

    data class MessagesProps(
        val basename: String,
        val encoding: String
    )
}
