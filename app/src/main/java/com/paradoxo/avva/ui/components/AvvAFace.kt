package com.paradoxo.avva.ui.components

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.R
import kotlinx.coroutines.delay


@Composable
fun AvvAFace(
    size: Dp,
    modifier: Modifier = Modifier
) {
    var timerBlink by remember { mutableLongStateOf(3000) }
    var isBlinking by remember { mutableStateOf(false) }

    val animatedValue by animateDpAsState(
        targetValue = if (isBlinking) 0.dp else size,
        animationSpec = tween(200),
        label = "blink animation"
    )
    LaunchedEffect(timerBlink) {
        delay(timerBlink)
        isBlinking = true
        delay(200)
        isBlinking = false
        timerBlink = (5000..10000).random().toLong()
        Log.e("AvvAFace", "next timer: $timerBlink")
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.avva_face_mouth),
            contentDescription = "AvvA logo mouth",
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.avva_face_eyes),
            contentDescription = "AvvA logo eyes",
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedValue),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Preview
@Composable
private fun AvvAFacePreview() {
    AvvAFace(300.dp)
}