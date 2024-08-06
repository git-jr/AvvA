package com.paradoxo.avva.ui.permissions

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.avva.util.PermissionUtils


@Composable
fun PermissionScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val permissionUtils = remember { PermissionUtils(context) }

    val overlayIsAllowed = permissionUtils.overlayIsAllowed
    val avvaIsDefaultAssistant = permissionUtils.defaultVoiceAssistantIsAvva()

    permissionUtils.defaultVoiceAssistantIsAvva().let {
        Log.d("PermissionScreenName", "Default voice assistant: $it")
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.size(100.dp))

        Text(
            "Para usar o Avva, você precisa conceder algumas permissões",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(36.dp))

        // 1 - Conceda permissão para sobrepor tela
        Row(
            modifier = Modifier
                .clickable { permissionUtils.openOverlayPermission() }
                .fillMaxWidth()
                .padding(16.dp)
                .sizeIn(minHeight = 56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(8f)
            ) {
                Text(
                    "1 - Permitir sobreposição de tela",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "Permite que o app exiba informações sobre outros apps",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = if (overlayIsAllowed) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.weight(2f)
            )
        }
        HorizontalDivider()

        // 2 - Selecione "Avva" como assistente de voz padrão
        Row(
            modifier = Modifier
                .clickable { permissionUtils.openAssistantSettings() }
                .fillMaxWidth()
                .padding(16.dp)
                .sizeIn(minHeight = 56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(8f)
            ) {
                Text(
                    "2 - Selecione \"Avva\" como assistente de voz padrão",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "Clique aqui e selecione \"Avva\" como App Assistente",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = if (avvaIsDefaultAssistant) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.weight(2f)
            )

        }
        HorizontalDivider()

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PermissionScreenPreview() {
    PermissionScreen()
}