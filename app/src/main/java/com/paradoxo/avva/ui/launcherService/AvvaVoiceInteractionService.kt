package com.paradoxo.avva.ui.launcherService

import android.content.Intent
import android.os.Handler
import android.service.voice.VoiceInteractionService
import com.paradoxo.avva.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel


class AvvaVoiceInteractionService : VoiceInteractionService() {
    private var serviceScope: CoroutineScope? = null

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AvvA)

        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Mantém o serviço rodando até que seja explicitamente parado
    }

    override fun onLaunchVoiceAssistFromKeyguard() {
        // Lidar com o lançamento de assistentes de voz a partir da tela de bloqueio
    }


    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(runnable)

        // Parar escuta de voz
//        speechToText.stopListening()

        // Cancelar coroutine scope
        serviceScope?.cancel()

    }
}