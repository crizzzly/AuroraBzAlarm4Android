package com.crost.aurorabzalarm.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.Constants.PADDING_L
import com.crost.aurorabzalarm.Constants.PADDING_S
import com.crost.aurorabzalarm.R
import com.crost.aurorabzalarm.ui.elements.PreviewAllCharts
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    val currentHpVals by remember { viewModel.latestHpState }
    val currentAceVals by remember { viewModel.latestAceState }
    val currentEpamVals by remember { viewModel.latestEpamState }
    val currentTime = viewModel.datetime
    val alarmSettingsVisible by remember {  viewModel.alarmSettingsVisible }

    val currentDuration = viewModel.currentDurationOfFlight

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

//    formatter.format(currentTime),
//    currentAceVals!!.bz,
//    currentHpVals!!.hpNorth ,
//    currentEpamVals!!.speed,
//    currentEpamVals!!.density,
//    currentEpamVals!!.temp,
//    currentDuration
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING



    try {
        Log.d(
            "Composable value",
            "HP: $currentTime - ${currentHpVals!!.hpNorth}\n" +
                    "ACE: $currentTime - ${currentAceVals!!.bz}\n" +
                    "Epam: $currentTime - ${df.format(currentEpamVals!!.speed)}"
        )
    } catch (e: NullPointerException) {
        Log.e("Composable value", e.toString())
    }

    MainScreen(
        formatter.format(currentTime),
        currentAceVals!!.bz,
        currentHpVals!!.hpNorth ,
        currentEpamVals!!.speed,
        currentEpamVals!!.density,
        currentEpamVals!!.temp,
        df.format(currentDuration) ,
        alarmSettingsVisible
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    time: String,
    bz: Double,
    hpNorth: Int,
    speed: Double,
    density: Double,
    temp: Double,
    currentDuration: String,
    showAlarmSettings: Boolean
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = { setAlarmSettingsVisible() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build, //Notifications
                            contentDescription = "open alarm settings"
                        )
                    }
                }
            )
        }
    ) { paddingVals ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .scrollable(scrollState, orientation = Orientation.Vertical)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, PADDING_L.dp)
            ) {
                Text(
                    time,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, PADDING_S.dp)
                )
                Text("$currentDuration Minutes from DISCOVR to Earth")


                PreviewAllCharts(
                    bz,
                    hpNorth.toDouble(),
                    speed,
                    density,
                    temp
                )
            }
        }
        if (showAlarmSettings) {
            AlarmSettingsDialog( )
        }
    }
}

    



@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_MASK,
)
@Composable
fun MainScreenPreview() {
    MainScreen(
        "02:22\n22.10.23",
        -15.5,
        25,
        358.6,
        56.2,
        123.4,
        "54.2",
        false
    )
}


fun setAlarmSettingsVisible(){
    val viewModel = ViewModelFactory.getDataViewModel()
    viewModel.setAlarmSettingsVisible()
}