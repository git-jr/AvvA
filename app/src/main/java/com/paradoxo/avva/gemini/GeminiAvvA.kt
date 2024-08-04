package com.paradoxo.avva.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

const val MODEL_NAME: String = "gemini-1.5-flash"

class GeminiAvvA(
    private val apiKey: String
) {
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chat: Chat

    init {
        loadModel()
    }

    private fun loadModel() {
        generativeModel = GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey
        )
        chat = generativeModel.startChat()
    }

    suspend fun requestResponse(
        prompt: String,
        image: Bitmap? = null,
        onSuccessful: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            generativeModel.generateContent(
                content {
                    image?.let { image(it) }
                    text(prompt)
                }
            ).let { response ->
                response.text?.let(onSuccessful)
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "An error occurred")
        }
    }
}