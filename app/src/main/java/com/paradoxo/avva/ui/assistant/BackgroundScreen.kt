package com.paradoxo.avva.ui.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.ui.components.AvvAFace

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
            AvvAFace(300.dp)

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