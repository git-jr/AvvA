package com.paradoxo.avva.ui.permissions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.paradoxo.avva.R
import com.paradoxo.avva.ui.theme.AvvATheme
import com.paradoxo.avva.util.PermissionUtils


@Composable
fun PermissionScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val permissionUtils = remember { PermissionUtils(context) }

    var overlayIsAllowed by remember { mutableStateOf(permissionUtils.checkOverlayPermission()) }
    var avvaIsDefaultAssistant by remember { mutableStateOf(permissionUtils.checkVoiceAssistantIsAvva()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        overlayIsAllowed = permissionUtils.checkOverlayPermission()
        avvaIsDefaultAssistant = permissionUtils.checkVoiceAssistantIsAvva()
    }


    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.avva_square_transparent),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        Text(
            stringResource(R.string.grant_permissions_avva_tittle),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.size(8.dp))

        // 1 - Conceda permissão para sobrepor tela
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { permissionUtils.openOverlayPermission() }
                .background(MaterialTheme.colorScheme.onBackground, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
                .sizeIn(minHeight = 56.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(9f)
            ) {
                Text(
                    stringResource(R.string.allow_screen_overlay),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    stringResource(R.string.allow_app_overlay),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.background
                )
            }
            Icon(
                imageVector = if (overlayIsAllowed) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.background
            )
        }

        // 2 - Selecione "Avva" como assistente de voz padrão
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { permissionUtils.openAssistantSettings() }
                .background(MaterialTheme.colorScheme.onBackground, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
                .sizeIn(minHeight = 56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(9f)
            ) {
                Text(
                    stringResource(R.string.select_avva_default_assistant),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    stringResource(R.string.select_avva_assistant_app),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.background
                )
            }

            Icon(
                imageVector = if (avvaIsDefaultAssistant) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.background
            )

        }

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBC18B)
@Composable
private fun PermissionScreenPreview() {
    AvvATheme {
        PermissionScreen()
    }
}