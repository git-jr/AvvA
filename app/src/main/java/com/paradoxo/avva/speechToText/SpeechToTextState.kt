package com.paradoxo.avva.speechToText

data class SpeechToTextState(
    val text: String = "",
    val isAvailable: Boolean = false,
    val isListening: Boolean = false,
    val error: Boolean = false,
    val detected: Boolean = false
)