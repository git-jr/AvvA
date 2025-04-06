package com.paradoxo.avva.ui.assistant

import android.graphics.Bitmap
import com.paradoxo.avva.model.Message


data class AssistantUiState(
    val chatList: List<Message> = emptyList(),
    val usePrintScreen: Boolean = false,
    val loadingResponse: Boolean = false,
    val printScreen: Bitmap? = null,
    val isListening: Boolean = false,
    val isErrorListening: Boolean = false,
    val entryText: String = "",
    val enableEdit: Boolean = true,
    val centerX: Int = 0,
    val centerY: Int = 0,
    val endYDown: Int = 0,
    val endYUp: Int = 0,
)