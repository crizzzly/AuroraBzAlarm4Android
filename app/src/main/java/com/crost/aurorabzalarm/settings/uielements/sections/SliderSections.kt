package com.crost.aurorabzalarm.settings.uielements.sections

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.settings.Settings
import com.crost.aurorabzalarm.settings.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HpSliderSection(
    con: Context,
    hpSliderVal: Float,
    settingsConfig: Settings
){
//    val hpSliderVal = remember { hpSliderV }
    val viewModel: SettingsViewModel = viewModel()
    val hpSliderState = remember {
        SliderState(
            initialValue = hpSliderVal,
            valueRange = settingsConfig.hpWarningLevel.minValue..settingsConfig.hpWarningLevel.maxValue,
            steps = 5,
            initialOnValueChange = {
                Log.d("Hp: initialValChange", it.toString())
                viewModel.updateHpState(it)
            },
            onValueChangeFinished = {
                settingsConfig.hpWarningLevel.currentValue = hpSliderVal
                Log.d("Hp: onValChangeFinish", "")
                viewModel.saveConfig(con, settingsConfig)
            }
        )
    }

    ListItem( // Slider HP
        headlineContent = { Text("Set Hemispheric Power Level") },
        trailingContent = { Text(hpSliderVal.toInt().toString()) },
        supportingContent = {
            Slider(state = hpSliderState)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BzSliderSection(
    con: Context,
    bzSliderVal: Float,
    settingsConfig: Settings
){
    Log.d("BzSliderSection",
        "current: sliderVal: $bzSliderVal, settings:${settingsConfig.hpWarningLevel.currentValue}")

    val viewModel: SettingsViewModel = viewModel()
    var bzVal by remember { mutableFloatStateOf(bzSliderVal) }

    val bzSliderState = remember {
        SliderState(
            initialValue = bzVal ,
            valueRange = settingsConfig.bzWarningLevel.minValue..settingsConfig.bzWarningLevel.maxValue,
            steps = 5,
            initialOnValueChange = {
                bzVal = it
                viewModel.updateBzState(it)
                settingsConfig.bzWarningLevel.currentValue = bzVal
                Log.d("Bz initialOnValueChange",
                    "it: $it\n" +
                            "bzVal: $bzVal\n" +
                            "settings: ${settingsConfig.bzWarningLevel.currentValue}")
            },
            onValueChangeFinished = {
                viewModel.saveConfig(con, settingsConfig)
                Log.d("onValueChangeFinished", "settings: ${settingsConfig.bzWarningLevel}")
            }
        )
    }

    ListItem(  // Slider Bz
        headlineContent = { Text("Set Bz Level") },
        trailingContent = { Text(bzSliderVal.toInt().toString()) },
        supportingContent = {
            Slider(state = bzSliderState)
        }
    )

}