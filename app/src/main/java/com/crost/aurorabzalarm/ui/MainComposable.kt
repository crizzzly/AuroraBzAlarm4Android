package com.crost.aurorabzalarm.ui

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.settings.SettingsViewModel
import com.crost.aurorabzalarm.ui.appbars.AuroraAppBar
import com.crost.aurorabzalarm.ui.panels.PreviewAllPanels
import com.crost.aurorabzalarm.utils.Constants.PADDING_L
import com.crost.aurorabzalarm.utils.Constants.PADDING_S
import com.crost.aurorabzalarm.utils.PermissionManager
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import com.crost.settingsscreen.settings.uielements.SettingsScreen
import java.math.RoundingMode
import java.text.DecimalFormat


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComposable(
    permissionManager: PermissionManager,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
) {
    val dataViewModel: DataViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()


    val context = LocalContext.current

    val permissionState by permissionManager.permissionState.collectAsState()

    Log.d("MainComposable VM", dataViewModel.toString())
    val currentHpVals by remember { dataViewModel.latestHpState }
    val currentAceVals by remember { dataViewModel.latestAceState }
    val currentEpamVals by remember { dataViewModel.latestEpamState }
    val currentTime = dataViewModel.datetime
    val datetime = dataViewModel.dateTimeString
    val settingsVisible by settingsViewModel.showSettings.observeAsState()

    val currentDuration = dataViewModel.currentDurationOfFlight

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    if (! permissionState) {
        if (permissionManager.hasPermission(context)) {
            permissionManager.onPermissionGranted()
        } else {
            permissionManager.requestPermission(context, permissionLauncher)
        }
    }

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

    if(settingsVisible!!){
        SettingsScreen()
    }
    else{
        MainScreen(
    //        formatter.format(currentTime),
            datetime,
            currentAceVals!!.bz,
            currentHpVals!!.hpNorth ,
            currentEpamVals!!.speed,
            currentEpamVals!!.density,
            currentEpamVals!!.temp,
            df.format(currentDuration) ,

        )
    }
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
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AuroraAppBar()
        }
    ) { paddingVals ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .background(Color(0xFF303030))
        ) {
            Row {


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


                    PreviewAllPanels(
                        bz,
                        hpNorth.toDouble(),
                        speed,
                        density,
                        temp
                    )
                }
            }
            // TODO: integrate navigation!
//            Row {
//                LogFileContent()
//            }
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
        "54.2"
    )
}


