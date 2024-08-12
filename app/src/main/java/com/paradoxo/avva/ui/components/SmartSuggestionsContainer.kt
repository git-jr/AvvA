package com.paradoxo.avva.ui.components

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.model.Action

@Composable
fun SmartSuggestionsContainer(
    listActions: List<Action>,
    onActionClick: (String) -> Unit
) {
    val context = LocalView.current.context
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            listActions.forEach { action ->
                val actionText = stringResource(id = action.text)
                val commandText = stringResource(id = action.command)
                SuggestionChip(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        true,
                        borderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        Toast.makeText(context, actionText, Toast.LENGTH_SHORT).show()
                        onActionClick(commandText)
                    },
                    label = {
                        Text(
                            actionText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 100.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    shape = CircleShape,
                    icon = {
                        action.icon?.let {
                            Icon(
                                painter = painterResource(id = action.icon),
                                contentDescription = actionText,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                )
            }
        }
    }
}