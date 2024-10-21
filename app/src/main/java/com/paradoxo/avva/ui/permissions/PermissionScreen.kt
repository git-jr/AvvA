package com.paradoxo.avva.ui.permissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.paradoxo.avva.R
import com.paradoxo.avva.ui.components.AvvAFace
import com.paradoxo.avva.ui.theme.AvvATheme

@Composable
fun PermissionScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val state: PermissionUiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkOverlayPermission()
        viewModel.checkVoiceAssistantIsAvva()
        viewModel.checkAccessibilityPermission()
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AvvAFace(250.dp)

        var expanded by remember { mutableStateOf(false) }
        val rotation = animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "rotation"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { expanded = !expanded },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.grant_permissions_avva_tittle),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .rotate(rotation.value)
                    .size(36.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ContainerPermissionName(
                    isAllowed = state.overlayIsAllowed,
                    title = R.string.allow_screen_overlay,
                    description = R.string.allow_app_overlay,
                    onClick = { viewModel.openOverlayPermission() },
                )

                ContainerPermissionName(
                    isAllowed = state.avvaIsDefaultAssistant,
                    title = R.string.select_avva_default_assistant,
                    description = R.string.select_avva_assistant_app,
                    onClick = { viewModel.openAssistantSettings() },
                )

                ContainerPermissionName(
                    isAllowed = state.avvaIsAccessibility,
                    title = R.string.allow_accessibility,
                    description = R.string.select_avva_accessibility,
                    onClick = { viewModel.showAccessibilityDialog(true) },
                )

            }
        }
        Spacer(modifier = Modifier.size(36.dp))
    }

    if (state.showDialogAccessibility) {
        AlertDialog(
            onDismissRequest = {
                viewModel.showAccessibilityDialog(false)
            },
            title = { Text(text = stringResource(R.string.accessibility_permission)) },
            text = { Text(stringResource(R.string.accessibility_permission_description)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showAccessibilityDialog(false)
                        viewModel.openAccessibilitySettings()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.showAccessibilityDialog(false) },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(stringResource(R.string.decline))
                }
            }
        )
    }
}


@Composable
private fun ContainerPermissionName(
    modifier: Modifier = Modifier,
    title: Int,
    description: Int,
    isAllowed: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .background(
                MaterialTheme.colorScheme.onBackground,
                shape = MaterialTheme.shapes.medium
            )
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
                stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )

            Text(
                stringResource(description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.background
            )
        }
        Icon(
            imageVector = if (isAllowed) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            modifier = Modifier.weight(1f),
            tint = MaterialTheme.colorScheme.background
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBC18B)
@Composable
private fun PermissionScreenPreview() {
    AvvATheme {
        PermissionScreen()
    }
}