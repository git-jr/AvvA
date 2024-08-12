package com.paradoxo.avva.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.paradoxo.avva.R


@Composable
fun ClipboardText(text: String) {
    val textToAnnotatedString = AnnotatedString(text)
    val clipboardManager = LocalClipboardManager.current
    clipboardManager.setText(textToAnnotatedString)
    val context = LocalContext.current
    Toast.makeText(context, stringResource(R.string.text_copied), Toast.LENGTH_SHORT).show()
}