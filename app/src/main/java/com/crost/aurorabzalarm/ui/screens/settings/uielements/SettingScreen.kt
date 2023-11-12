package com.crost.aurorabzalarm.ui.screens.settings.uielements

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

data class SettingsState(
    var notificationEnabled: Boolean,
    var bzThreshold: Float,
    var hpThreshold: Float
)


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {

    SettingsScreen()
}

@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { SettingsAppBar() },
    ) { paddingVals ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
        ) {
//            paddingVals.calculateRightPadding(LayoutDirection.Ltr)
            SettingsList() }
    }
}




