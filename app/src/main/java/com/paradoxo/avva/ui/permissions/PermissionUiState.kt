package com.paradoxo.avva.ui.permissions


data class PermissionUiState(
    val overlayIsAllowed: Boolean = false,
    val avvaIsDefaultAssistant: Boolean = false,
    val avvaIsAccessibility: Boolean = false,
    val showDialogAccessibility: Boolean = false,
)