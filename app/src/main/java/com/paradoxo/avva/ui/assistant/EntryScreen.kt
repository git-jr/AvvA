package com.paradoxo.avva.ui.assistant

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.paradoxo.avva.R
import com.paradoxo.avva.model.listActions
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.components.ChatComponent
import com.paradoxo.avva.ui.components.EditTextEntry
import com.paradoxo.avva.ui.components.SmartSuggestionsContainer
import com.paradoxo.avva.ui.theme.AvvATheme
import com.paradoxo.avva.util.PermissionUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EntryScreen(
    state: AssistantUiState,
    onSend: (String) -> Unit = {},
    defaultShowContent: Boolean = false,
    onToggleUsePrintScreen: () -> Unit = {},
    onToggleListening: () -> Unit = {},
    onUpdateEntryText: (String) -> Unit = {}
) {
    // create a viewModel for this screen in the future
    val context = LocalContext.current
    val permissionUtils = remember { PermissionUtils(context) }

    var micPermissionGranted by remember { mutableStateOf(false) }
    var requestMicPermission by remember { mutableStateOf(false) }


    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        micPermissionGranted = permissionUtils.checkMicPermission()
    }

    val scope = rememberCoroutineScope()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        micPermissionGranted = isGranted
        if (micPermissionGranted) {
            onToggleListening()
        }
    }

    if (requestMicPermission) {
        LaunchedEffect(Unit) {
            scope.launch {
                delay(200)
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    var showContent by remember { mutableStateOf(defaultShowContent) }
    LaunchedEffect(Unit) {
        delay(10)
        showContent = true
    }

    AnimatedVisibility(
        visible = showContent,
        enter = expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        )
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .sizeIn(minHeight = 180.dp)
                    .animateContentSize()
                    .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.large)
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.onBackground,
                        RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    AnimatedVisibility(
                        visible = state.usePrintScreen,
                        enter = expandVertically(
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutVertically() + shrinkVertically() + fadeOut()
                    ) {
                        SmartSuggestionsContainer(listActions) {
                            onSend(it)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.screen_content),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = state.usePrintScreen,
                            onCheckedChange = { onToggleUsePrintScreen() },
                            colors = SwitchDefaults.colors().copy(
                                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                                checkedTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.5f
                                ),
                                uncheckedThumbColor = MaterialTheme.colorScheme.background,
                                uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.5f
                                ),
                                uncheckedBorderColor = Color.Transparent,
                            )
                        )
                    }
                }

                ChatComponent(state.chatList, Modifier.sizeIn(maxHeight = 300.dp))

                Column {
                    if (state.loadingResponse) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    val interactionSource = remember { MutableInteractionSource() }
                    EditTextEntry(
                        state = state,
                        enableEdit = state.enableEdit,
                        interactionSource = interactionSource,
                        onToggleListening = {
                            if (micPermissionGranted) {
                                onToggleListening()
                            } else {
                                requestMicPermission = true
                            }
                        },
                        onUpdateEntryText = onUpdateEntryText,
                        onSend = onSend
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFE8AA75)
@Composable
private fun EntryScreenPreview() {
    AvvATheme {
        EntryScreen(
            state = AssistantUiState(
                chatList = sampleMessageList,
            ),
            defaultShowContent = true
        )
    }
}