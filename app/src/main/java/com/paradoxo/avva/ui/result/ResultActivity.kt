package com.paradoxo.avva.ui.result

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradoxo.avva.R
import com.paradoxo.avva.getLastSavedImage
import com.paradoxo.avva.model.Action
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status
import com.paradoxo.avva.model.SuggestionAction
import com.paradoxo.avva.model.markdownContent
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

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                getLastSavedImage()?.let { imageBitmap ->
                    MainScreen(imageBitmap)
                    EntryScreen()
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Falha ao carregar imagem\nTente novamente?",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable { recreate() }
                                .background(Color.White)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WindowInsetsSetup() {
        val windowInsetsController: WindowInsetsControllerCompat
        val view = LocalView.current
        val window = (view.context as Activity).window
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

    }
}

@Composable
fun MainScreen(imageBitmap: Bitmap) {
    Image(
        modifier = Modifier.fillMaxSize(),
        bitmap = imageBitmap
            .asImageBitmap(),
        contentDescription = "Imagem salva",
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    defaultResult: String? = null,
    chatList: List<Message> = remember { mutableStateListOf() },
    showSmartActions: Boolean = true
) {
    val viewModel = hiltViewModel<ResultViewModel>()
    val state by viewModel.uiState.collectAsState()


//    var chatList by remember { mutableStateOf(mutableListOf<String>()) }
    val chatListSample: SnapshotStateList<Message> = remember { mutableStateListOf() }
    chatListSample.addAll(chatList)

    var editState by remember { mutableStateOf("") }
    var enableEdit by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    var showLoading by remember { mutableStateOf(false) }


    var simulateLoading by remember { mutableStateOf(false) }
    LaunchedEffect(simulateLoading) {
        if (simulateLoading) {
            showLoading = true
            enableEdit = false
            delay(500)

            val randomResult = LoremIpsum(100).values.first()
            chatListSample.add(Message(editState, Status.USER))
            chatListSample.add(Message(randomResult + markdownContent, Status.AI))

            editState = ""
            showLoading = false
            enableEdit = true
            simulateLoading = false
        }
    }

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
                .background(Color.White, MaterialTheme.shapes.large),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            if (showSmartActions) {
                SmartSuggestionsContainer(listActions)
            }

            ChatComponent(chatListSample, Modifier.sizeIn(maxHeight = 300.dp))
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
                                        "O que você deseja fazer?",
                                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp)
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
                                simulateLoading = true
                                viewModel.getResponse(editState)
                            }
                    )
                }
            }

        }
    }
}

@Composable
private fun SmartSuggestionsContainer(
    listActions: List<Action>
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
                SuggestionChip(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = {
                        Toast.makeText(context, action.text, Toast.LENGTH_SHORT).show()
                    },
                    label = {
                        Text(
                            action.text,
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
                                contentDescription = action.text,
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
        MainScreen(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        EntryScreen(
            chatList = sampleMessageList
        )
    }
}


val listActions = listOf(
    Action("Explique a tela", SuggestionAction.EXPLAIN, R.drawable.ic_explain_screen),
    Action("Verificar informação", SuggestionAction.CHECK_INFO, R.drawable.ic_check_info),
    Action("Traduzir", SuggestionAction.TRANSLATE, R.drawable.ic_translate),
    Action("Outra ação", SuggestionAction.SMART_REPLY, R.drawable.ic_actions),
)