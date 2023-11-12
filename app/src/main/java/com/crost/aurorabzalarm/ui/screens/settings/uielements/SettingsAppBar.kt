package com.crost.aurorabzalarm.ui.screens.settings.uielements

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(){
    val viewModel: SettingsViewModel = viewModel()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text("Aurora Alarm Settings") },
        navigationIcon = {
            IconButton(
                onClick = { viewModel.setSettingsVisible(false)}
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "close alarm settings"
                )
            }
        }
    )
}