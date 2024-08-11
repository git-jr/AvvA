package com.paradoxo.avva.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString


@Composable
fun ClipboardText(text: String) {
    val textToAnnotatedString = AnnotatedString(text)
    val clipboardManager = LocalClipboardManager.current
    clipboardManager.setText(textToAnnotatedString)
    val context = LocalContext.current
    Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
}