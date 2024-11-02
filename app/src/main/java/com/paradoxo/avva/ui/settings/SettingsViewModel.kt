package com.paradoxo.avva.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.dataStore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: UserPreferencesDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadApiKey()
    }

    private fun loadApiKey() {
        viewModelScope.launch {
            dataStore.getApiKey().collect {
                _uiState.value = _uiState.value.copy(apiKey = it)
            }
        }
    }

    fun setApiKey(apiKey: String) {
        _uiState.value = _uiState.value.copy(apiKey = apiKey)
    }

    fun saveApiKey() {
        if (_uiState.value.apiKey.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.value.apiKey.let {
                dataStore.saveApiKey(it)
                goToHome(true)
            }
        }
    }

    fun goToHome(goToHome: Boolean) {
        _uiState.value = _uiState.value.copy(goToHome = goToHome)
    }
}