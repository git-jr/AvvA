package com.paradoxo.avva.ui.result

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paradoxo.avva.R
import com.paradoxo.avva.model.Action
import com.paradoxo.avva.model.listActions
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.components.ChatComponent
import com.paradoxo.avva.ui.theme.AvvATheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    state: ResultUiState,
    onSend: (String) -> Unit = {},
    onToggleUsePrintScreen: () -> Unit = {},
    defaultShowContent: Boolean = false
) {
    var editState by remember { mutableStateOf("") }
    var enableEdit by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(state.loadingResponse) {
        Log.d("EntryScreen", "loadingResponse true: ${state.loadingResponse}")
        if (state.loadingResponse) {
            showLoading = true
            enableEdit = false
        } else {
            Log.d("EntryScreen", "loadingResponse false: ${state.loadingResponse}")
            editState = ""
            showLoading = false
            enableEdit = true
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
                    if (showLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BasicTextField(
                            modifier = Modifier.weight(9f),
                            value = editState,
                            onValueChange = { editState = it },
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
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            decorationBox = @Composable { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = editState,
                                    innerTextField = innerTextField,
                                    enabled = enableEdit,
                                    singleLine = false,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSource,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.what_to_do),
                                            style = MaterialTheme.typography.displaySmall.copy(
                                                fontSize = 24.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onBackground
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

                        IconButton(
                            onClick = { onSend(editState) },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

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
            state = ResultUiState(
                chatList = sampleMessageList,
            ),
            defaultShowContent = true
        )
    }
}