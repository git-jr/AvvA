package com.paradoxo.avva.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.R

@Composable
fun BackgroundScreen(
    modifier: Modifier = Modifier,
    text: String? = "AvvA"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDC48C)),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.avva_square_transparent),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )

            text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFE8AA75)
                )
            }
        }

        Spacer(modifier = Modifier.size(50.dp))
    }
}


@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OptionsScreenPreview() {
    BackgroundScreen()
}