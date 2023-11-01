package com.crost.aurorabzalarm.ui.appbars

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
import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory

fun closeSettingScreen() {
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.setSettingsVisible(false)
}
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text("Aurora Alarm Settings") },
        navigationIcon = {
            IconButton(
                onClick = { closeSettingScreen()}
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "close alarm settings"
                )
            }
        }
    )
}