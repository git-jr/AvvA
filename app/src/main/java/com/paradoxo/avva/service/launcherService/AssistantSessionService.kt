package com.paradoxo.avva.service.launcherService

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.service.voice.VoiceInteractionSession
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.paradoxo.avva.ui.AssistantActivity
import com.paradoxo.avva.util.BitmapUtil
import kotlinx.coroutines.Job


class AssistantSessionService(private val context: Context?) :
    VoiceInteractionSession(context) {

    private lateinit var handler: Handler

    private val job = Job()

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
        screenshot?.let { screenshotBitmap ->
            context?.let { BitmapUtil(context).saveBitmapOnInternalStorageApp(screenshotBitmap) }
            openAssistantActivity()
        } ?: run {
            Log.e("AssistantSessionService", "Screenshot is null")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}