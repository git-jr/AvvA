package com.paradoxo.avva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Status
import com.paradoxo.avva.model.sampleMessageList
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun ChatComponent(
    chatList: List<Message>,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true
    ) {
        items(chatList.reversed()) { message ->
            val isUser = Status.USER == message.status
            val isAI = Status.AI == message.status
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
            ) {
                if (isUser) {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp, 8.dp)
                ) {
                    if (isAI) {
                        ItemChatAI(message)
                    } else {
                        ItemChatUser(message)
                    }
                }

                if (isAI) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }

        }
    }
}

@Composable
private fun ItemChatUser(message: Message) {
    MarkdownText(
        message.text,
        style = TextStyle(
            color = Color.Black,
            textAlign = TextAlign.Justify,
        ),
        modifier = Modifier
            .background(Color.LightGray.copy(0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp, 8.dp)
    )
}


@Composable
private fun ItemChatAI(message: Message) {
    MarkdownText(
        message.text,
        style = TextStyle(
            color = Color.Black,
            textAlign = TextAlign.Justify,
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ChatComponentPreview() {
    val chatListSample: SnapshotStateList<Message> = remember { mutableStateListOf() }
    chatListSample.addAll(sampleMessageList)
    ChatComponent(chatList = chatListSample)
}