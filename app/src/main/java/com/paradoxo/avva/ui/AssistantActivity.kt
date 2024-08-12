package com.paradoxo.avva.ui

import android.app.Activity
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.paradoxo.avva.ui.assistant.AssistantScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupEntryAndExitTransition()

        setContent {
            WindowInsetsSetup()
            AssistantScreen()
        }
    }

    private fun setupEntryAndExitTransition() {
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
    }

    @Composable
    private fun WindowInsetsSetup() {
        val view = LocalView.current
        val window = (view.context as Activity).window
        window.navigationBarColor = MaterialTheme.colorScheme.background.copy(0.2f).toArgb()
    }
}


