package com.paradoxo.avva.ui.assistant

import android.util.DisplayMetrics
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.dataStore.UserPreferencesDataStore
import com.paradoxo.avva.gemini.GeminiAvvA
import com.paradoxo.avva.model.Author
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.service.accessibilityService.clickAccessibilityService
import com.paradoxo.avva.speechToText.SpeechToText
import com.paradoxo.avva.util.ActionHandler
import com.paradoxo.avva.util.BitmapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val gemini: GeminiAvvA,
    private val bitmapUtil: BitmapUtil,
    private val actionHandler: ActionHandler,
    private val speechToText: SpeechToText,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadPrintScreen()
        viewModelScope.launch {
            speechToText.state.collect { speechState ->
                _uiState.update {
                    it.copy(
                        entryText = speechState.text,
                        isListening = speechState.isListening,
                        isErrorListening = speechState.error
                    )
                }
            }
        }

        viewModelScope.launch {
            delay(500)
            toggleListening(false)
        }

        checkUseCustomApiKey()
    }

    private fun checkUseCustomApiKey() {
        viewModelScope.launch {
            dataStore.getApiKey().collect {
                if (it.isNotEmpty()) {
                    gemini.setCustomApiKey(it)
                }
            }
        }
    }

    private fun loadPrintScreen() {
        viewModelScope.launch {
            bitmapUtil.getLastSavedImage().let {
                _uiState.value = _uiState.value.copy(printScreen = it)
            }
        }
    }

    fun getResponse(searchPrompt: String, rawPrompt: String) {
        _uiState.value = _uiState.value.copy(loadingResponse = true, enableEdit = false)
        addMessage(Message(rawPrompt, Author.USER))

        viewModelScope.launch {
            gemini.chatRequestResponse(
                prompt = searchPrompt,
                history = _uiState.value.chatList,
                image = if (_uiState.value.usePrintScreen) _uiState.value.printScreen else null,
                onSuccessful = { response ->
                    if (response.contains("findSound")) {
                        val musicName = response.substringAfter("findSound").substringBefore("}")
                            .replace(":", "")
                            .replace("\"", "")
                        Log.d("AssistantViewModelResponse", "music name: $musicName")
                        actionHandler.playYTMusic(musicName) {
                            Log.d("AssistantViewModel", "Music playing")
                            viewModelScope.launch {
                                delay(2000)
                                clickAccessibilityService?.click(500, 500)
                            }
                        }

                        return@chatRequestResponse
                    } else {
                        addMessage(Message(response, Author.AI))
                    }
                    Log.d("AssistantViewModelResponse", "Response: $response")
                },
                onFailure = {
                    Log.e("AssistantViewModelResponse", "Error: $it")
                    addMessage(Message("An error occurred", Author.AI))
                }
            )

        }.invokeOnCompletion {
            _uiState.value = _uiState.value.copy(
                loadingResponse = false,
                entryText = "",
                enableEdit = true
            )
        }
    }


    private fun addMessage(message: Message) {
        _uiState.value = _uiState.value.copy(
            chatList = _uiState.value.chatList + message
        )
    }

    fun toggleUsePrintScreen() {
        _uiState.value = _uiState.value.copy(usePrintScreen = !_uiState.value.usePrintScreen)
    }


    fun toggleListening(notifyNothingListened: Boolean = false) {
        Log.e("SpeechToText", "startListening chamado no viewmodel ${uiState.value.isListening}")
        if (uiState.value.isListening) {
            speechToText.stopListening()
        } else {
            speechToText.startListening(notifyNothingListened)
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechToText.close()
    }

    fun updateEntryText(text: String) {
        _uiState.value = _uiState.value.copy(entryText = text)
    }
}