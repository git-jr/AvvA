package com.paradoxo.avva.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status

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
        val config = generationConfig {
            temperature = 0.9f
        }

        generativeModel = GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey,
            generationConfig = config
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

    suspend fun chatRequestResponse(
        prompt: String,
        history: List<Message>,
        image: Bitmap? = null,
        onSuccessful: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val chat = generativeModel.startChat(
                history = history.map {
                    content(role = if (it.status == Status.AI) "model" else "user") { text(it.text) }
                }
            )

            chat.sendMessage(
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