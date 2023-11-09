package com.crost.aurorabzalarm.settings.uielements

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.settings.SettingsViewModel
import com.crost.aurorabzalarm.settings.uielements.sections.BzSliderSection
import com.crost.aurorabzalarm.settings.uielements.sections.HeadlineSection
import com.crost.aurorabzalarm.settings.uielements.sections.HpSliderSection
import com.crost.aurorabzalarm.settings.uielements.sections.NotificationStateSection



@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsList() {
    val con = LocalContext.current
    val viewModel: SettingsViewModel = viewModel()
    val settingsConfig = viewModel.loadAndReturnConfig(LocalContext.current)
    val hpSliderVal = viewModel.hpSliderState.observeAsState(
        initial = settingsConfig.hpWarningLevel.currentValue
    )
    val bzSliderVal = viewModel.bzSliderState.observeAsState(
        initial = settingsConfig.bzWarningLevel.currentValue
    )
    val notificationEnabled = viewModel.notificationEnabled.observeAsState(
        initial = settingsConfig.notificationEnabled
    )

    Log.d(
        "SettingsScreen",
        "settingsState, notification: ${notificationEnabled}\n" +
                "hpSliderVal: ${hpSliderVal.value}, bzSliderVal: ${bzSliderVal.value}"
    )


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        NotificationStateSection(
            con = con,
            notificationEnabled = notificationEnabled.value,
            settingsConfig = settingsConfig,
            columnScope = this
        )

        HeadlineSection()
        BzSliderSection(
            con,
            bzSliderVal = bzSliderVal.value,
            settingsConfig = settingsConfig
        )

        HpSliderSection(
            con = con,
            hpSliderVal = hpSliderVal.value,
            settingsConfig = settingsConfig
        )
    }
}