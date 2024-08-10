package com.paradoxo.avva.ui.launcherService

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.service.voice.VoiceInteractionSession
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.paradoxo.avva.ui.assistant.AssistantActivity
import com.paradoxo.avva.util.BitmapUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AssistantSessionService(private val context: Context?) : VoiceInteractionSession(context) {

    private lateinit var handler: Handler

    private val job = Job()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()


    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
    }


    private fun openAssistantActivity() {
        context?.let {
            val intent = Intent(context, AssistantActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(context, intent, null)
            finish()
        }
    }

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        Log.d("onHandleScreenshot42", "onHandleScreenshot")
        screenshot?.let { screenshotBitmap ->
            Log.d("onHandleScreenshot42", "Print disponível")
            context?.let { BitmapUtil(context).saveBitmapOnInternalStorageApp(screenshotBitmap) }
            openAssistantActivity()
        } ?: run {
            Log.d("onHandleScreenshot42", "Náo printou")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}


data class UiState(
    val text: String = "Teste"
)