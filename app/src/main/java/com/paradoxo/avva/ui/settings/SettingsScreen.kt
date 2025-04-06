package com.paradoxo.avva.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradoxo.avva.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
    onGoToHome: () -> Unit
) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()

    val viewModel = hiltViewModel<SettingsViewModel>()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.goToHome) {
        if (state.goToHome) {
            onGoToHome()
            viewModel.goToHome(false)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    title = { Text(stringResource(R.string.custom_key)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.aviso_1_acesso_api_key),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.aviso_2_acesso_api_key),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.aviso_3_acesso_api_key),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.apiKey,
                onValueChange = { viewModel.setApiKey(it) },
                label = { Text(stringResource(R.string.key)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.saveApiKey()
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.5f
                    ),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalView.current.context

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    viewModel.saveApiKey()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                ),
            ) {
                Row(
                    Modifier
                        .clickable { onOpenApiSite(context) }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key),
                            "key",
                            Modifier.padding(4.dp),
                            tint = Color.White,
                        )
                    }

                    Text(
                        stringResource(R.string.gerar_chave_no_site_oficial),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

        }
    }
}

fun onOpenApiSite(context: Context) {
    val uriString = "https://aistudio.google.com/app/apikey"
    val intent = Intent(Intent.ACTION_VIEW, uriString.toUri())
    context.startActivity(intent)
}