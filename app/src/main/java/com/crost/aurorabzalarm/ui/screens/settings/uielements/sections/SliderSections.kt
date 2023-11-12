package com.crost.aurorabzalarm.ui.screens.settings.uielements.sections

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.ListItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.screens.settings.Settings
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import java.math.RoundingMode
import java.text.DecimalFormat


fun getRoundedIntAsString(floatVal: Float): String? {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(floatVal)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HpSliderSection(
    con: Context,
    sliderVal: Float,
    settingsConfig: Settings
){
    var hpSliderVal by remember { mutableFloatStateOf(sliderVal) }
    val viewModel: SettingsViewModel = viewModel()


    val hpSliderState = remember {
        SliderState(
            initialValue = hpSliderVal,
            valueRange = settingsConfig.hpWarningLevel.minValue..settingsConfig.hpWarningLevel.maxValue,
            steps = 5,
            initialOnValueChange = {
                hpSliderVal = it
                viewModel.updateHpState(it)
            },
            onValueChangeFinished = {
                settingsConfig.hpWarningLevel.currentValue = hpSliderVal
                viewModel.saveConfig(con, settingsConfig)
            }
        )
    }
    hpSliderState.value = hpSliderVal


//    val colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)

    ListItem( // Slider HP
        headlineContent = { Text("Set Hemispheric Power Level") },
        supportingContent = {
            SettingsSlider(sliderState = hpSliderState, sliderVal = hpSliderVal)
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BzSliderSection(
    con: Context,
    sliderVal: Float,
    settingsConfig: Settings
){
    val viewModel: SettingsViewModel = viewModel()
    var bzSliderVal by remember { mutableFloatStateOf(sliderVal) }

    val bzSliderState = remember {
        SliderState(
            initialValue = bzSliderVal ,
            valueRange = settingsConfig.bzWarningLevel.minValue..settingsConfig.bzWarningLevel.maxValue,
            steps = 5,
            initialOnValueChange = {
                bzSliderVal = it
                viewModel.updateBzState(it)
                settingsConfig.bzWarningLevel.currentValue = bzSliderVal
            },
            onValueChangeFinished = {
                viewModel.saveConfig(con, settingsConfig)
            }
        )
    }

    bzSliderState.value = bzSliderVal

    ListItem(  // Slider Bz
        headlineContent = { Text("Set Bz Level") },
        supportingContent = {
            SettingsSlider(sliderState = bzSliderState, sliderVal = bzSliderVal)
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSlider(sliderState: SliderState, sliderVal: Float){
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
//    val colors = SliderDefaults.colors(thumbColor = colorInactive, activeTrackColor = colorInactive)
    val sliderValString = getRoundedIntAsString(sliderVal) ?: 0
    Slider(
        modifier = Modifier.fillMaxWidth(),
        state = sliderState,
        thumb = {
            Label(
                label = {
                    PlainTooltip(
                        modifier = Modifier
                            .requiredSize(45.dp, 50.dp)
//                                    .padding(0.dp, 0.dp, 0.dp, 50.dp)
                            .wrapContentWidth()
                            .fillMaxHeight()
                    ) {
                    }
                },
                interactionSource = interactionSource
            ) {
                Box {
                    Text(
                        text = sliderValString as String,
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 50.dp)
                    )
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
//                        colors = colors,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },

        track = {
            SliderDefaults.Track(
//                colors = colors,
                sliderState = sliderState
            )
        }
    )
}