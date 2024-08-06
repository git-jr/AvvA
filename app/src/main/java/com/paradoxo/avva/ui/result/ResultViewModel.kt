package com.paradoxo.avva.ui.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.gemini.GeminiAvvA
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status
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
    private val bitmapUtil: BitmapUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResultUiState()
    )

    var uiState = _uiState.asStateFlow()

    init {
        loadPrintScreen()
    }

    fun loadPrintScreen() {
        viewModelScope.launch {
            bitmapUtil.getLastSavedImage().let {
                _uiState.value = _uiState.value.copy(printScreen = it)
            }
        }
    }

    fun getResponse(prompt: String) {
        _uiState.value = _uiState.value.copy(loadingResponse = true)
        addMessage(Message(prompt, Status.USER))

        viewModelScope.launch {
            gemini.requestResponse(
                prompt = prompt,
                image = if (_uiState.value.usePrintScreen) _uiState.value.printScreen else null,
                onSuccessful = { response ->
                    Log.d("ResultViewModelResponse", "Response: $response")
                    addMessage(Message(response, Status.AI))
                },
                onFailure = {
                    Log.e("ResultViewModelResponse", "Error: $it")
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


