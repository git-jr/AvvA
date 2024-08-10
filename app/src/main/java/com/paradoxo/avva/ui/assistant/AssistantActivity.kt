package com.paradoxo.avva.ui.assistant

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradoxo.avva.R
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.theme.AvvATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupEntryAndExitTransition()

        setContent {
            WindowInsetsSetup()
            AvvATheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val viewModel = hiltViewModel<AssistantViewModel>()
                    val state: AssistantUiState by viewModel.uiState.collectAsState()

                    if (state.printScreen == null) {
                        BackgroundScreen(text = stringResource(R.string.no_image_yet))
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BackgroundScreen()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        Crossfade(
                            targetState = state.usePrintScreen,
                            label = "show print screen"
                        ) { showImage ->
                            if (showImage) {
                                state.printScreen?.let { imageBitmap ->
                                    PrintScreen(imageBitmap)
                                }
                            }
                        }

                        EntryScreen(
                            state = state,
                            onToggleUsePrintScreen = { viewModel.toggleUsePrintScreen() },
                            onSend = { prompt ->
                                viewModel.getResponse(
                                    getString(
                                        R.string.handle_music_prompt,
                                        prompt
                                    )
                                )
                            },
                            onToggleListening = { viewModel.toggleListening() },
                            onUpdateEntryText = { viewModel.updateEntryText(it) }
                        )
                    }
                }
            }
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


@Composable
fun PrintScreen(imageBitmap: Bitmap) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap
                .asImageBitmap(),
            contentDescription = "Imagem salva",
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
    }

}


@Preview(showBackground = true, backgroundColor = 0xFFE8AA75)
@Composable
fun MainScreenPreview() {
    AvvATheme {
        PrintScreen(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        EntryScreen(
            state = AssistantUiState(
                chatList = sampleMessageList
            ),
            defaultShowContent = true
        )
    }
}

