package com.paradoxo.avva.speechToText


import android.content.Context
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SpeechToText @Inject constructor(
    private val context: Context
) : RecognitionListener {

    private val _state = MutableStateFlow(SpeechToTextState())
    val state = _state.asStateFlow()

    private val recognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    init {
        checkIsAvailable(false)
    }

    private fun checkIsAvailable(notify: Boolean) {
        val isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        _state.update {
            it.copy(
                isAvailable = isAvailable,
                error = notify && !isAvailable
            )
        }
    }

    fun startListening(notifyNothingListened: Boolean = false) {
        resetState(notifyNothingListened)

        val isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        if (isAvailable) {
            val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayOf("pt-BR", "en-US"))
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            recognizer.setRecognitionListener(this)
            recognizer.startListening(intent)
            _state.update { it.copy(isListening = true) }
        } else {
            checkIsAvailable(notifyNothingListened)
        }
    }


    fun stopListening() {
        recognizer.stopListening()
        recognizer.cancel()
        _state.update { it.copy(isListening = false) }
    }

    private fun resetState(notifyNothingListened: Boolean) {
        _state.update {
            it.copy(
                text = "",
                error = false,
                notifyNothingListened = notifyNothingListened
            )
        }
    }

    fun close() {
        recognizer.stopListening()
        recognizer.destroy()
    }

    override fun onReadyForSpeech(params: android.os.Bundle?) {
        _state.update { it.copy(error = false) }
    }

    override fun onError(error: Int) {
        if (SpeechRecognizer.ERROR_CLIENT != error) {
            _state.update {
                it.copy(error = _state.value.notifyNothingListened, isListening = false)
            }
        }
    }

    override fun onResults(results: android.os.Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.firstOrNull()?.let { text ->
            _state.update { it.copy(text = text, isListening = false) }
        }
    }

    override fun onPartialResults(partialResults: android.os.Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.firstOrNull()?.let { text ->
            _state.update { it.copy(text = text) }
        }
    }

    override fun onBeginningOfSpeech() {
        _state.update { it.copy(isListening = true, error = false) }
    }


    override fun onEndOfSpeech() {
        _state.update { it.copy(isListening = false) }
    }

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEvent(eventType: Int, params: android.os.Bundle?) = Unit
}