package com.paradoxo.avva.ui.result

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.gemini.GeminiAvvA
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val gemini: GeminiAvvA
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResultUiState()
    )

    var uiState = _uiState.asStateFlow()

    fun getResponse(prompt: String, printScreen: Bitmap? = null) {
        addMessage(Message(prompt, Status.USER))
        _uiState.value = _uiState.value.copy(loadingResponse = true)

        viewModelScope.launch {
            gemini.requestResponse(
                prompt = prompt,
                image = printScreen,
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
}


