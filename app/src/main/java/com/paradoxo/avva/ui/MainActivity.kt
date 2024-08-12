package com.paradoxo.avva.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.paradoxo.avva.service.launcherService.AvvaVoiceInteractionService
import com.paradoxo.avva.ui.main.HomeScreen
import com.paradoxo.avva.ui.theme.AvvATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startInteractionService()
        enableEdgeToEdge()

        setContent {
            WindowInsetsSetup()
            AvvATheme {
                HomeScreen(
                    openTutorial = {
                        openTutorial()
                    },
                    openAssistantActivity = {
                        openAssistantActivity()
                    }
                )
            }
        }
    }

    @Composable
    private fun WindowInsetsSetup() {
        val view = LocalView.current
        val window = (view.context as Activity).window
        window.navigationBarColor = MaterialTheme.colorScheme.background.copy(0.2f).toArgb()
    }

    private fun startInteractionService() {
        val voiceServiceIntent = Intent(this, AvvaVoiceInteractionService::class.java)
        voiceServiceIntent.setAction(Intent.ACTION_ASSIST)

        Handler(Looper.getMainLooper()).postDelayed({
            startService(voiceServiceIntent)
        }, 1000)
    }

    private fun openAssistantActivity() {
        val intent = Intent(this, AssistantActivity::class.java)
        startActivity(
            intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }

    private fun openTutorial() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://paradoxo.tech/avva")
        startActivity(intent)
    }
}