package com.paradoxo.avva.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BackgroundScreen() {
    val firstColor = Color(0xFF722CFF)
    val secondColor = Color(0xFF00BFA5)

    val brush = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(secondColor, firstColor),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0f, 0.95f)
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "AvvA",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}


@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OptionsScreenPreview() {
    BackgroundScreen()
}