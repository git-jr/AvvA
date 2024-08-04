package com.paradoxo.avva.ui.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.gemini.GeminiAvvA
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
        ResultUiState(
            result = ""
        )
    )

    var uiState = _uiState.asStateFlow()

    fun getResponse(prompt: String) {
        viewModelScope.launch {
            gemini.requestResponse(
                prompt = prompt,
                onSuccessful = {
                   Log.d("ResultViewModelResponse", "Response: $it")
                    _uiState.value = _uiState.value.copy(
                        result = it
                    )
                },
                onFailure = {
                    Log.e("ResultViewModelResponse", "Error: $it")
                }
            )
        }
    }
}


data class ResultUiState(
    val result: String
)