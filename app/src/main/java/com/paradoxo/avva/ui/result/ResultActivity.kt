package com.paradoxo.avva.ui.result

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradoxo.avva.R
import com.paradoxo.avva.model.Action
import com.paradoxo.avva.model.listActions
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.components.ChatComponent
import com.paradoxo.avva.ui.theme.AvvATheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupEntryAndExitTransition()

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                val viewModel = hiltViewModel<ResultViewModel>()
                viewModel.loadPrintScreen(this@ResultActivity)
                val state: ResultUiState by viewModel.uiState.collectAsState()

                if (state.printScreen == null) {
                    BackgroundScreen(text = stringResource(R.string.no_image_yet))
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(
                        targetState = state.usePrintScreen,
                        label = "show print screen"
                    ) { showImage ->
                        if (showImage) {
                            state.printScreen?.let { imageBitmap ->
                                PrintScreen(imageBitmap)
                            }
                        } else {
                            BackgroundScreen()
                        }
                    }

                    EntryScreen(
                        state = state,
                        onToggleUsePrintScreen = { viewModel.toggleUsePrintScreen() },
                        onSend = { prompt -> viewModel.getResponse(prompt) },
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    state: ResultUiState,
    onSend: (String) -> Unit = {},
    onToggleUsePrintScreen: () -> Unit = {},
) {
    var editState by remember { mutableStateOf("") }
    var enableEdit by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(state.loadingResponse) {
        if (state.loadingResponse) {
            showLoading = true
            enableEdit = false
        } else {
            editState = ""
            showLoading = false
            enableEdit = true
        }
    }

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(showContent) {
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
                    .background(Color.White, MaterialTheme.shapes.large),
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
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.screen_content),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)
                        )
                        Switch(
                            checked = state.usePrintScreen,
                            onCheckedChange = { onToggleUsePrintScreen() },
                        )
                    }
                }

                ChatComponent(state.chatList, Modifier.sizeIn(maxHeight = 300.dp))

                Column {
                    if (showLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BasicTextField(
                            value = editState,
                            onValueChange = { editState = it },
                            singleLine = false,
                            maxLines = 3,
                            interactionSource = interactionSource,
                            textStyle = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
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
                                            )
                                        )
                                    },
                                    colors = TextFieldDefaults.colors().copy(
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        cursorColor = Color.Black,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                    ),
//                                contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                )
                            }
                        )

                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.Gray,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(24.dp)
                                .clickable {
                                    onSend(editState)
                                }
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
                    onClick = {
                        Toast.makeText(context, actionText, Toast.LENGTH_SHORT).show()
                        onActionClick(commandText)
                    },
                    label = {
                        Text(
                            actionText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 100.dp)
                        )
                    },
                    shape = CircleShape,
                    icon = {
                        action.icon?.let {
                            Icon(
                                painter = painterResource(id = action.icon),
                                contentDescription = actionText,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF4CAF50,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Composable
fun MainScreenPreview() {
    AvvATheme {
        PrintScreen(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        EntryScreen(
            state = ResultUiState(
                chatList = sampleMessageList
            )
        )
    }
}