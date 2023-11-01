
package com.crost.aurorabzalarm.settings

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.ui.appbars.SettingsAppBar
import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory

data class SettingsState(
    var notificationEnabled: Boolean,
    var bzThreshold: Int,
    var hpThreshold: Int
)

@Composable
fun SettingsScreenWrapper(settingsViewModel: SettingsViewModel){
    val con = LocalContext.current
    val settingsConfig = settingsViewModel.settingsConfig
    val hp = settingsViewModel.hpSliderState.observeAsState(initial = settingsConfig.hpWarningLevel.currentValue)
    val bz = settingsViewModel.bzSliderState.observeAsState(initial = settingsConfig.bzWarningLevel.currentValue)

    val notification = settingsViewModel.notificationEnabled.observeAsState(initial = settingsConfig.notificationEnabled)

    SettingsScreen(notification.value, hp.value, bz.value, settingsConfig)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    notificationEnabled: Boolean,
    hpSliderVal: Float,
    bzSliderVal: Float,
    settingsConfig: Settings
) {

    Log.d("SettingsScreen",
        "settingsState, notification: ${notificationEnabled}\n" +
            "hpSliderVal: $hpSliderVal, bzSliderVal: $bzSliderVal")

    val hpSliderState = remember {
        SliderState(
            initialValue = hpSliderVal,
            valueRange = 0f..100f,
            steps = 5,
            initialOnValueChange = {
                onHpThresholdChange(it)
            },
            onValueChangeFinished = {
                settingsConfig.hpWarningLevel.currentValue = hpSliderVal
                saveSettingsToFile(settingsConfig)
            }
        )
    }


    val bzSliderState = remember {
        SliderState(
            initialValue = bzSliderVal,
            valueRange = -30.0f..0.0f,
            steps = 5,
            initialOnValueChange = {
                onBzThresholdChange(it)
            },
            onValueChangeFinished = {
                settingsConfig.bzWarningLevel.currentValue = hpSliderVal
                saveSettingsToFile(settingsConfig)
            }
        )
    }




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
                ListItem( // Enable/Disable
                    headlineContent = {
                        Text(
                            "Enable Aurora Alarm",
                            textAlign = TextAlign.Right
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange =
                            { checked ->
                                Log.d("Settings creen", "alarm switch enabled : $checked")
                                setNotificationState(checked)
                                settingsConfig.notificationEnabled = checked
                                saveSettingsToFile(settingsConfig)
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                )
                ListItem(  // Headline Warning Level
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

                ListItem(  // Slider Bz
                    headlineContent = { Text("Set Bz Warning Level") },
                    trailingContent = { Text(bzSliderVal.toInt().toString()) },
                    supportingContent = {
                        Slider(state = bzSliderState)
                    }
                )

                ListItem( // Slider HP
                    headlineContent = { Text("Set Hemispheric Power Warning Level") },
                    trailingContent = { Text(hpSliderVal.toInt().toString()) },
                    supportingContent = {
                        // Slider HP
                        Slider(state = hpSliderState)
                    }
                )
            }
        }
    }
}


fun setNotificationState(checked: Boolean) {
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.setNotificationState(checked)
}

fun onBzThresholdChange( value: Float){
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.updateBzState(value)
}

fun onHpThresholdChange( value: Float){
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.updateHpState(value)
}


fun saveSettingsToFile(settings: Settings){
    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
    viewModel.saveConfigToFile(settings)
}



//@Preview(
//    showBackground = true,
//)
//@Composable
//fun SettingsScreenPreview() {
//    val notificationEnabled = remember { mutableStateOf(false) }
//    val hpSliderVal = remember { mutableStateOf(50f) }
//    val bzSliderVal = remember { mutableStateOf(-15f) }
//
//    SettingsScreen(
//        notificationEnabled.value, hpSliderVal.value, bzSliderVal.value, settingsConfig
//    )
//}