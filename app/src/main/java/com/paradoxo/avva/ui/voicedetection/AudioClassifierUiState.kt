package com.paradoxo.avva.ui.voicedetection

import com.google.mediapipe.tasks.components.containers.Category

data class AudioClassifierUiState(
    val results: List<Category> = emptyList(),
    val error: String? = null,
)