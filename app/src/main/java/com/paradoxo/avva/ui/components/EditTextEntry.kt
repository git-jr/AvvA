package com.paradoxo.avva.ui.components

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paradoxo.avva.R
import com.paradoxo.avva.ui.assistant.AssistantUiState


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditTextEntry(
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
                            contentDescription = if (isListening) stringResource(R.string.stop_listening) else stringResource(
                                R.string.hear
                            ),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
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
                            contentDescription = stringResource(R.string.send),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}