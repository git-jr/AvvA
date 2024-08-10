package com.paradoxo.avva.ui.permissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.rotate
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
    var avvaIsAccessibility by remember { mutableStateOf(permissionUtils.checkAccessibilityPermission()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        overlayIsAllowed = permissionUtils.checkOverlayPermission()
        avvaIsDefaultAssistant = permissionUtils.checkVoiceAssistantIsAvva()
        avvaIsAccessibility = permissionUtils.checkAccessibilityPermission()
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
                // 1 - Overlay screen permission
                ContainerPermissionName(
                    isAllowed = overlayIsAllowed,
                    title = R.string.allow_screen_overlay,
                    description = R.string.allow_app_overlay,
                    onClick = { permissionUtils.openOverlayPermission() },
                )

                // 2 - Default assistant permission
                ContainerPermissionName(
                    isAllowed = avvaIsDefaultAssistant,
                    title = R.string.select_avva_default_assistant,
                    description = R.string.select_avva_assistant_app,
                    onClick = { permissionUtils.openAssistantSettings() },
                )

                // 3 - Accessibility permission
                ContainerPermissionName(
                    isAllowed = avvaIsAccessibility,
                    title = R.string.allow_accessibility,
                    description = R.string.select_avva_accessibility,
                    onClick = { permissionUtils.openAccessibilitySettings() },
                )

            }
        }
        Spacer(modifier = Modifier.size(36.dp))
    }
}


@Composable
fun ContainerPermissionName(
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