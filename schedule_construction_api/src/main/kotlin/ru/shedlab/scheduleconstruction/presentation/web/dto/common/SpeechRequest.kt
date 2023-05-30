package ru.shedlab.scheduleconstruction.presentation.web.dto.common

data class SpeechRequest(
    val lang: String = "en-EN",
    val format: String = "mp3",
    val speed: String = "1.0",
    val speaker: String = "alyss",
    val text: String,
)
