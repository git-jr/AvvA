package com.paradoxo.avva.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ChatComponent(
    chatList: SnapshotStateList<String>,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        items(chatList.size) { index ->
            Text(
                text = chatList[index],
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Black
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ChatComponentPreview() {
    val chatListSample = remember { mutableStateListOf("") }
    chatListSample.add("Hello")
    chatListSample.add("World")
    ChatComponent(chatList = chatListSample)
}