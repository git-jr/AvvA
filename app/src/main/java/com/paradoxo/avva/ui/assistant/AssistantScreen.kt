package com.paradoxo.avva.ui.assistant

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradoxo.avva.R
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.components.BackgroundScreen
import com.paradoxo.avva.ui.components.PrintViewComponent
import com.paradoxo.avva.ui.theme.AvvATheme


@Composable
fun AssistantScreen() {
    AvvATheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            val viewModel = hiltViewModel<AssistantViewModel>()
            val state: AssistantUiState by viewModel.uiState.collectAsState()
            val context = LocalView.current.context

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
                            PrintViewComponent(imageBitmap)
                        }
                    }
                }

                EntryScreen(
                    state = state,
                    onToggleUsePrintScreen = { viewModel.toggleUsePrintScreen() },
                    onSend = { prompt ->
                        viewModel.getResponse(
                            context.getString(R.string.handle_music_prompt, prompt),
                            prompt
                        )
                    },
                    onToggleListening = { viewModel.toggleListening(true) },
                    onUpdateEntryText = { viewModel.updateEntryText(it) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE8AA75)
@Composable
fun AssistantScreenPreview() {
    AvvATheme {
        PrintViewComponent(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        EntryScreen(
            state = AssistantUiState(
                chatList = sampleMessageList
            ),
            defaultShowContent = true
        )
    }
}