package com.paradoxo.avva.speechToText


import android.content.Context
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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
        Log.d("SpeechToText47", "no checkIsAvailable isAvailable é: $isAvailable")
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
            Log.d("SpeechToText47", "startListening isAvailable é true")
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
            Log.d("SpeechToText47", "startListening isAvailable é false")
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
        Log.d("SpeechToText47", "onReadyForSpeech")
        _state.update { it.copy(error = false) }
    }


    override fun onError(error: Int) {
        Log.d("SpeechToText47", "onReadyForSpeech")

        if (SpeechRecognizer.ERROR_CLIENT != error) {
            _state.update {
                it.copy(error = _state.value.notifyNothingListened, isListening = false)
            }
        }
    }

    override fun onResults(results: android.os.Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d("SpeechToText47", "onResults $matches")
        matches?.firstOrNull()?.let { text ->
            _state.update { it.copy(text = text, isListening = false) }
        }
    }

    override fun onPartialResults(partialResults: android.os.Bundle?) {
        Log.d("SpeechToText47", "onPartialResults: inicio")
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.firstOrNull()?.let { text ->
            Log.d("SpeechToText47", "onPartialResults: $text")
            _state.update { it.copy(text = text) }
        }
    }

    override fun onBeginningOfSpeech() {
        Log.d("SpeechToText47", "onBeginningOfSpeech")
        _state.update { it.copy(isListening = true, error = false) }
    }


    override fun onEndOfSpeech() {
        Log.d("SpeechToText47", "onEndOfSpeech")
        _state.update { it.copy(isListening = false) }
    }

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEvent(eventType: Int, params: android.os.Bundle?) = Unit
}