package com.paradoxo.avva.ui.result

import android.graphics.Bitmap
import com.paradoxo.avva.model.Message


data class ResultUiState(
    val chatList: List<Message> = emptyList(),
    val usePrintScreen: Boolean = true,
    val loadingResponse: Boolean = false,
    val printScreen: Bitmap? = null
)