package com.paradoxo.avva.ui.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.gemini.GeminiAvvA
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.accessibilityService.clickAccessibilityService
import com.paradoxo.avva.util.ActionHandler
import com.paradoxo.avva.util.BitmapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val gemini: GeminiAvvA,
    private val bitmapUtil: BitmapUtil,
    private val actionHandler: ActionHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResultUiState()
    )

    var uiState = _uiState.asStateFlow()

    init {
        loadPrintScreen()
    }

    private fun loadPrintScreen() {
        viewModelScope.launch {
            bitmapUtil.getLastSavedImage().let {
                _uiState.value = _uiState.value.copy(printScreen = it)
            }
        }
    }

    fun getResponse(prompt: String) {
        val customPrompt = "Se o prompt a seguir indicar uma solicitação para tocar, ouvir ou reproduzir uma música, retorne um JSON no formato '{findSound: \"nome da música\"}'. Caso contrário, responda normalmente, fornecendo uma resposta informativa e relevante ao contexto da pergunta.\nConteúdo do prompt: $prompt"

        _uiState.value = _uiState.value.copy(loadingResponse = true)
        addMessage(Message(prompt, Status.USER))

        viewModelScope.launch {
            gemini.chatRequestResponse(
                prompt = customPrompt,
                history = _uiState.value.chatList,
                image = if (_uiState.value.usePrintScreen) _uiState.value.printScreen else null,
                onSuccessful = { response ->
                    if (response.contains("findSound:")) {
                        addMessage(Message("Abrindo o YouTube...", Status.AI))
                        val musicName = response.substringAfter("findSound:").substringBefore("}").trim()
                        actionHandler.playYTMusic(musicName) {
                            Log.d("ResultViewModel", "Music playing")
                            viewModelScope.launch {
                                delay(2000)
                                clickAccessibilityService?.click(500, 500)
                            }
                        }

                        return@chatRequestResponse
                    }
                    Log.d("ResultViewModelResponse", "Response: $response")
                    addMessage(Message(response, Status.AI))
                },
                onFailure = {
                    Log.e("ResultViewModelResponse", "Error: $it")
                    addMessage(Message("An error occurred", Status.AI))
                }
            )

        }.invokeOnCompletion {
            _uiState.value = _uiState.value.copy(loadingResponse = false)
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
}


