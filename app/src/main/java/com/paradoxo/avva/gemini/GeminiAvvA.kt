package com.paradoxo.avva.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Author

const val MODEL_NAME: String = "gemini-1.5-flash"

class GeminiAvvA(
    private var apiKey: String
) {
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chat: Chat

    init {
        loadModel()
    }

    private fun loadModel(customKey: String = apiKey) {
        val config = generationConfig {
            temperature = 0.9f
        }

        generativeModel = GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = customKey,
            generationConfig = config
        )
        chat = generativeModel.startChat()
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
                    content(role = if (it.author == Author.AI) "model" else "user") { text(it.text) }
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

    fun setCustomApiKey(key: String) {
        loadModel(key)
    }
}