package com.paradoxo.avva.ui.result

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.paradoxo.avva.getLastSavedImage
import com.paradoxo.avva.ui.theme.AvvATheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center

                ) {
                    getLastSavedImage()?.let { imageBitmap ->
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = imageBitmap
                                .asImageBitmap(),
                            contentDescription = "Imagem salva",
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Text(
                            text = "Nenhuma imagem salva",
                            modifier = Modifier
                                .fillMaxSize()
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
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
//            .size(24.dp)
//            .background(color = Color.Transparent)
//            .padding(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AvvATheme {
        Greeting2("Android")
    }
}