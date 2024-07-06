package com.paradoxo.avva

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.paradoxo.avva.gemini.Gemini
import com.paradoxo.avva.ui.launcherService.AvvaVoiceInteractionService
import com.paradoxo.avva.ui.theme.AvvATheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val voiceServiceIntent = Intent(this, AvvaVoiceInteractionService::class.java)
        voiceServiceIntent.setAction(Intent.ACTION_ASSIST)

        Handler(Looper.getMainLooper()).postDelayed({
            startService(voiceServiceIntent)
        }, 1000)

//        val intent = Intent(this, OverlayService::class.java)
//        startService(intent)
//
//        val accessibilityService = Intent(this, MyAccessibilityService::class.java)
//        startService(accessibilityService)


        val gemini = Gemini()
        gemini.setupModel(
            apiKey = ""
        )

        lifecycleScope.launch {
            gemini.sendPromptChat(
                prompt = "Qual a ultima versao do android?",
                onResponse = {
                    Log.d("GeminiTest", "Gemini response: $it")
                }
            )
        }

        setContent {
            AvvATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AvvATheme {
        Greeting("Android")
    }
}