package com.paradoxo.avva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.model.Message
import com.paradoxo.avva.model.Author
import com.paradoxo.avva.model.sampleMessageList
import com.paradoxo.avva.ui.theme.AvvATheme
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun ChatComponent(
    chatList: List<Message>,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        reverseLayout = true
    ) {
        items(chatList.reversed()) { message ->
            val isUser = Author.USER == message.author
            val isAI = Author.AI == message.author
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
            ) {
                if (isUser) {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
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
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.onBackground.copy(0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp, 8.dp)
    )
}


@Composable
private fun ItemChatAI(message: Message) {
    var copyText by remember { mutableStateOf<String?>(null) }
    copyText?.let { ClipboardText(it) }
    MarkdownText(
        message.text,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = Modifier.clickable {
            copyText = message.text
        }
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

    AvvATheme {
        ChatComponent(chatList = chatListSample)
    }
}