package com.paradoxo.avva

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.ui.launcherService.AvvaVoiceInteractionService
import com.paradoxo.avva.ui.permissions.PermissionScreen
import com.paradoxo.avva.ui.result.ResultActivity
import com.paradoxo.avva.ui.theme.AvvATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voiceServiceIntent = Intent(this, AvvaVoiceInteractionService::class.java)
        voiceServiceIntent.setAction(Intent.ACTION_ASSIST)

        Handler(Looper.getMainLooper()).postDelayed({
            startService(voiceServiceIntent)
        }, 1000)


//        startActivity(Intent(this, ResultActivity::class.java))

//
//        val accessibilityService = Intent(this, MyAccessibilityService::class.java)
//        startService(accessibilityService)


        enableEdgeToEdge()
        setContent {
            AvvATheme {
                MainScreen() {
                    openResultActivity()
                }
            }
        }
    }


    private fun openResultActivity() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(
            intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }

}


@Composable
private fun MainScreen(
    openResultActivity: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(32.dp))

            PermissionScreen()

            Spacer(modifier = Modifier.size(32.dp))

            Button(onClick = { openResultActivity() }) {
                Text("Result Activity")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MainScreenPreview() {
    AvvATheme {
        MainScreen {}
    }
}