package com.paradoxo.avva.service.launcherService

import android.content.Intent
import android.service.voice.VoiceInteractionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel


class AvvaVoiceInteractionService : VoiceInteractionService() {
    private var serviceScope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        // migrate to voice service to here in the future
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onLaunchVoiceAssistFromKeyguard() {}


    override fun onDestroy() {
        super.onDestroy()
        serviceScope?.cancel()
    }
}