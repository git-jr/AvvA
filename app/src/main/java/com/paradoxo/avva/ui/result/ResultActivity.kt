package com.paradoxo.avva.ui.result

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.paradoxo.avva.R
import com.paradoxo.avva.getLastSavedImage
import com.paradoxo.avva.model.Action
import com.paradoxo.avva.model.SuggestionAction
import com.paradoxo.avva.ui.theme.AvvATheme

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
fun EntryScreen() {
    var editState by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 200.dp, maxHeight = 250.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
        ) {
            SmartSuggestionsContainer(listActions)
            Column(
                modifier = Modifier
                    .imePadding(),
            ) {
                Text(
                    text = "Resultado",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))

                var enabled by remember { mutableStateOf(true) }
                val interactionSource = remember { MutableInteractionSource() }

                BasicTextField(
                    value = editState,
                    onValueChange = { editState = it },
                    singleLine = true,
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = @Composable { innerTextField ->
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = editState,
                            innerTextField = innerTextField,
                            enabled = enabled,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            placeholder = { Text("O que você deseja fazer?") },
                            colors = TextFieldDefaults.colors().copy(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                cursorColor = Color.Black,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            contentPadding = OutlinedTextFieldDefaults.contentPadding()

                        )
                    }
                )


            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                OutlinedButton(
                    onClick = { },
                ) {
                    Text(
                        text = "Enviar",
                        modifier = Modifier.padding(4.dp),
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
        EntryScreen()
    }
}


val listActions = listOf(
    Action("Explique a tela", SuggestionAction.EXPLAIN, R.drawable.ic_launcher_background),
    Action("Verificar informação", SuggestionAction.CHECK_INFO, R.drawable.ic_launcher_background),
    Action("Outra ação", SuggestionAction.SMART_REPLY, R.drawable.ic_launcher_background),
)