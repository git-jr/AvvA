package com.paradoxo.avva

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.paradoxo.avva.gemini.Gemini
import com.paradoxo.avva.ui.launcherService.AvvaVoiceInteractionService
import com.paradoxo.avva.ui.theme.AvvATheme
import kotlinx.coroutines.launch
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.paradoxo.avva.ui.result.ResultActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voiceServiceIntent = Intent(this, AvvaVoiceInteractionService::class.java)
        voiceServiceIntent.setAction(Intent.ACTION_ASSIST)

        Handler(Looper.getMainLooper()).postDelayed({
            startService(voiceServiceIntent)
        }, 1000)


        startActivity(Intent(this, ResultActivity::class.java))

//        val intent = Intent(this, OverlayService::class.java)
//        startService(intent)
//
//        val accessibilityService = Intent(this, MyAccessibilityService::class.java)
//        startService(accessibilityService)


//        val gemini = Gemini()
//        gemini.setupModel(
//            apiKey = ""
//        )
//
//        lifecycleScope.launch {
//            gemini.sendPromptChat(
//                prompt = "Qual a ultima versao do android?",
//                onResponse = {
//                    Log.d("GeminiTest", "Gemini response: $it")
//                }
//            )
//        }

        enableEdgeToEdge()
        setContent {
            AvvATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Greeting(
                            name = "Avva",
                            modifier = Modifier.padding(innerPadding)
                        )

                        Spacer(modifier = Modifier.padding(16.dp))
//                        Button(onClick = { openActivityAssistant() }) {
//                            Text("Open Assistant")
//                        }

                        // botão para abrir as configurações do celular que permite selcionar um novo assistente de voz
                        Button(onClick = { openAssistantSettings() }) {
                            Text("Open Assistant Settings")
                        }

                        Spacer(modifier = Modifier.size(32.dp))

                        Button(onClick = { openResultActivity() }) {
                            Text("Result Activity")
                        }

                    }
                }
            }
        }
    }

    fun openResultActivity() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    fun openActivityAssistant() {
        val voiceServiceIntent = Intent(this, AvvaVoiceInteractionService::class.java)
//        voiceServiceIntent.setAction(Intent.ACTION_VOICE_COMMAND )
//        voiceServiceIntent.setAction(Intent.ACTION_ASSIST )
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            startService(voiceServiceIntent)
//        }, 500)

//        val intent = Intent(Intent.ACTION_VOICE_COMMAND)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
    }

    fun openAssistantSettings() {
        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
        startActivity(intent)
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