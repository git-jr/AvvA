package com.paradoxo.avva.ui.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradoxo.avva.util.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionUtils: PermissionUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())

    var uiState = _uiState.asStateFlow()

    init {
        checkAllPermissions()
    }

    private fun checkAllPermissions() {
        checkOverlayPermission()
        checkVoiceAssistantIsAvva()
        checkAccessibilityPermission()
    }

    fun checkOverlayPermission() {
        viewModelScope.launch {
            _uiState.update { it.copy(overlayIsAllowed = permissionUtils.checkOverlayPermission()) }
        }
    }

    fun checkVoiceAssistantIsAvva() {
        viewModelScope.launch {
            _uiState.update { it.copy(avvaIsDefaultAssistant = permissionUtils.checkVoiceAssistantIsAvva()) }
        }
    }

    fun checkAccessibilityPermission() {
        viewModelScope.launch {
            _uiState.update { it.copy(avvaIsAccessibility = permissionUtils.checkAccessibilityPermission()) }
        }
    }

    fun openOverlayPermission() {
        permissionUtils.openOverlayPermission()
    }

    fun openAssistantSettings() {
        permissionUtils.openAssistantSettings()
    }

    fun openAccessibilitySettings() {
        permissionUtils.openAccessibilitySettings()
    }

}