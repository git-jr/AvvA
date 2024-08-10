package com.paradoxo.avva.ui.assistant

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.paradoxo.avva.R
import com.paradoxo.avva.model.Action
import com.paradoxo.avva.model.listActions
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.components.ChatComponent
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
    val interactionSource = remember { MutableInteractionSource() }
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
                    EditText(
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditText(
    state: AssistantUiState,
    enableEdit: Boolean,
    interactionSource: MutableInteractionSource,
    onUpdateEntryText: (String) -> Unit,
    onToggleListening: () -> Unit,
    onSend: (String) -> Unit
) {

    val localContext = LocalView.current.context
    LaunchedEffect(state.isErrorListening) {
        if (state.isErrorListening) {
            Toast.makeText(
                localContext,
                localContext.getString(R.string.error_listening),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            modifier = Modifier.weight(9f),
            value = state.entryText,
            onValueChange = { onUpdateEntryText(it) },
            singleLine = false,
            enabled = enableEdit,
            maxLines = 2,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                )
            ),
            textStyle = MaterialTheme.typography.displaySmall.copy(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = state.entryText,
                    innerTextField = innerTextField,
                    enabled = enableEdit,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = {
                        Text(
                            stringResource(if (state.isListening) R.string.listening else R.string.what_to_do),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.8f
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.5f
                        ),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                )
            }
        )

        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = state.isListening,
                label = ""
            ) { isListening ->
                if (state.entryText.isBlank()) {
                    IconButton(
                        onClick = { onToggleListening() },
                    ) {
                        Icon(
                            painterResource(if (isListening) R.drawable.ic_stop else R.drawable.ic_mic),
                            contentDescription = if (isListening) "Parar de ouvir" else "Ouvir",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.background( MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
            Crossfade(
                targetState = !state.isListening && state.entryText.isNotBlank(),
                label = ""
            ) { show ->
                if (show) {
                    IconButton(
                        onClick = { onSend(state.entryText) }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.background( MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SmartSuggestionsContainer(
    listActions: List<Action>,
    onActionClick: (String) -> Unit
) {
    val context = LocalView.current.context
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            listActions.forEach { action ->
                val actionText = stringResource(id = action.text)
                val commandText = stringResource(id = action.command)
                SuggestionChip(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        true,
                        borderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        Toast.makeText(context, actionText, Toast.LENGTH_SHORT).show()
                        onActionClick(commandText)
                    },
                    label = {
                        Text(
                            actionText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 100.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    shape = CircleShape,
                    icon = {
                        action.icon?.let {
                            Icon(
                                painter = painterResource(id = action.icon),
                                contentDescription = actionText,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                )
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