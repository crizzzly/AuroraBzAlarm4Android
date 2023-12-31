package com.crost.aurorabzalarm.ui.screens.settings.uielements

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import com.crost.aurorabzalarm.ui.screens.settings.uielements.sections.BzSliderSection
import com.crost.aurorabzalarm.ui.screens.settings.uielements.sections.HpSliderSection
import com.crost.aurorabzalarm.ui.screens.settings.uielements.sections.NotificationStateSection
import com.crost.aurorabzalarm.ui.screens.settings.uielements.sections.SettingsHeadlineSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsList() {
    val con = LocalContext.current
    val viewModel: SettingsViewModel = viewModel()
    val settingsConfig = viewModel.loadAndReturnConfig(LocalContext.current)
    val hpSliderVal = viewModel.hpSliderState.observeAsState(
        initial = settingsConfig.hpWarningLevel.currentValue
    )
    var bzSliderVal = viewModel.bzSliderState.observeAsState(
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

        SettingsHeadlineSection()
        BzSliderSection(
            con,
            bzSliderVal.value,
            settingsConfig = settingsConfig
        )

        HpSliderSection(
            con = con,
            sliderVal = hpSliderVal.value,
            settingsConfig = settingsConfig
        )
    }
}