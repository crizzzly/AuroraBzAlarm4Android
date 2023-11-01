@file:Suppress("UNUSED_EXPRESSION")

package com.crost.aurorabzalarm.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.ui.appbars.SettingsAppBar
import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory

data class SettingsState(
    var notificationsEnabled: Boolean,
    var settingsVisible: Boolean,
    var bzThreshold: Int,
    var hpThreshold: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
//    settingsState: SettingsState,
//    onNotificationsEnabledChange: (Boolean) -> Unit,
//    onBzThresholdChange: (Int) -> Unit,
//    onHpThresholdChange: (Int) -> Unit
) {
    val settingsViewModel = AuroraViewModelFactory.getSettingsViewModel()
    val settingsState = settingsViewModel.settingsState.observeAsState()
    val hpSliderVal by settingsViewModel.hpSliderState.collectAsState()
    val bzSliderVal by settingsViewModel.bzSliderState.collectAsState()

    var hpSliderState: SliderState
    hpSliderState = remember {
        SliderState(
            initialValue = hpSliderVal,
            initialOnValueChange = {


                                   onHpThresholdChange(it)
            },
            valueRange = 0f..100f,
            onValueChangeFinished = {

                // launch some business logic update with the state you hold
            },
            steps = 5
        )
    }

    hpSliderState.value = hpSliderVal

    val bzSliderState = SliderState(
        initialValue = bzSliderVal,
        valueRange = -30.0f..0.0f,
        initialOnValueChange = {
            onBzThresholdChange(it)
        }
    )

    bzSliderState.value = bzSliderVal




    Scaffold(
        topBar = { SettingsAppBar() },
    ) { paddingVals ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            "Enable Aurora Alarm",
                            textAlign = TextAlign.Right
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = settingsState.value!!.notificationsEnabled,
                            onCheckedChange =
                            { checked ->
                                setNotificationState(checked)
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                )
                ListItem(
                    headlineContent = { Text("Warning Levels") },
                    colors = ListItemColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        headlineColor = MaterialTheme.colorScheme.primary,
                        supportingTextColor = MaterialTheme.colorScheme.primary,
                        leadingIconColor = MaterialTheme.colorScheme.primaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledHeadlineColor = MaterialTheme.colorScheme.secondary,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.secondary,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.secondary,
                        overlineColor = MaterialTheme.colorScheme.secondary,

                    )
                )

                ListItem(
                    headlineContent = { Text("Set Bz Warning Level") },
                    trailingContent = { Text(bzSliderState.value.toString()) },
                    supportingContent = {
                        Column {

                        }
                    }
                )

                ListItem(
                    headlineContent = { Text("Set Hemispheric Power Warning Level") },
                    trailingContent = { Text(hpSliderState.value.toString()) },
                    supportingContent = {
                        Slider(
                            state = hpSliderState,

                            )
                    }
                )
            }
        }
    }
}



fun setNotificationState(checked: Boolean) {
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.setNotificationsState(checked)
}

fun onBzThresholdChange( value: Float){
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.updateBzState(value)
}

fun onHpThresholdChange( value: Float){
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.updateHpState(value)
}

@Preview(
    showBackground = true,
    )
@Composable
fun SettingsScreenPreview() {
    val settingsState = rememberUpdatedState(
        SettingsState(
            true,
            true,
            -10,
            50
        )
    )

    SettingsScreen(
//        settingsState = settingsState,
//        onNotificationsEnabledChange = { /* Handle notifications enabled state */ },
//        onBzThresholdChange = { /* Handle Bz threshold change */ },
//        onHpThresholdChange = { /* Handle HP threshold change */ }
    )
}
