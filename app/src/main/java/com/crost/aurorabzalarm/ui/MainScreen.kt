package com.crost.aurorabzalarm.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.appbars.AuroraAppBar
import com.crost.aurorabzalarm.ui.panels.ShowAllPanels
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import java.math.RoundingMode
import java.text.DecimalFormat


private const val DEBUG = false

@Composable
fun MainScreen() {
    val scrollState = rememberScrollState()
    val dataViewModel: DataViewModel = viewModel()

    val currentSolarWindData by remember { dataViewModel.latestSolarWindData }
    val currentImfData by remember { dataViewModel.latestImfData }
    val currentKpAlert by remember { dataViewModel.kpAlertState }
    val currentKpWarning by remember { dataViewModel.kpWarningState }
    val currentSolarStormAlert by remember { dataViewModel.solarStormState }

    val currentTime = dataViewModel.dateTime
    val time = dataViewModel.dateTimeString
    val currentDuration = dataViewModel.currentDurationOfFlight

    val debugString = "Current Vals: bz: ${currentImfData.bz}, " +
            "speed: ${currentSolarWindData.speed},\n" +
            "KpAlert: ${currentKpAlert.message}, \n" +
            "KpWarning: ${currentKpWarning.message}"+"\n" +
            "SolarStorm, ${currentSolarStormAlert.message}" +"\n"


    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING



    if (DEBUG){
        try {
            Log.d(
                "Composable value",
                debugString
            )
        } catch (e: NullPointerException) {
            Log.e("Composable value", e.toString())
        }
    }

    Scaffold(
        topBar = {
            AuroraAppBar()
        }
    ) { paddingVals ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .verticalScroll(state = scrollState)
                .background(Color(0xFF303030))
        ) {
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, SpaceWeatherDataConstants.PADDING_L.dp)
                ) {
                    Text(
                        time,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, SpaceWeatherDataConstants.PADDING_S.dp)
                    )
                    Text("$currentDuration Minutes from DISCOVR to Earth")

                    ShowAllPanels(
                        currentImfData.bz,
                        currentImfData.bt,
                        currentSolarWindData.speed,
                        currentSolarWindData.density,
                        currentSolarWindData.temperature
                    )
                    Text(text = debugString, color = Color.LightGray)
                }
            }
        }
    }
}


@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_MASK,
)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
