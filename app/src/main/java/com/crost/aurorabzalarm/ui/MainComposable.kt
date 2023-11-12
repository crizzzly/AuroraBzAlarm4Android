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
import com.crost.aurorabzalarm.settings.uielements.SettingsScreen
import com.crost.aurorabzalarm.ui.appbars.AuroraAppBar
import com.crost.aurorabzalarm.ui.panels.PreviewAllPanels
import com.crost.aurorabzalarm.utils.Constants.PADDING_L
import com.crost.aurorabzalarm.utils.Constants.PADDING_S
import com.crost.aurorabzalarm.utils.PermissionManager
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

const val DEBUG = false

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComposable(
    permissionManager: PermissionManager,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
) {
    val context = LocalContext.current

    val dataViewModel: DataViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val permissionState by permissionManager.permissionState.collectAsState()
    val settingsVisible by settingsViewModel.showSettings.observeAsState()

    val currentSolarWindData by remember { dataViewModel.latestSolarWindData }
    val currentImfData by remember { dataViewModel.latestImfData }
    val currentKpAlert by remember { dataViewModel.kpAlertState }
    val currentKpWarning by remember { dataViewModel.kpWarningState }
    val currentSolarStormAlert by remember { dataViewModel.solarStormState }

    val currentTime = dataViewModel.dateTime
    val dateTimeString = dataViewModel.dateTimeString
    val currentDuration = dataViewModel.currentDurationOfFlight
    
    val debugString = "Current Vals: bz: ${currentImfData!!.bz}, " +
            "speed: ${currentSolarWindData!!.speed},\n" +
            "KpAlert: ${currentKpAlert.message}, \n" +
            "KpWarning: ${currentKpWarning.message}"+"\n" +
            "SolarStorm, ${currentSolarStormAlert.message}" +"\n"


    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    if (! permissionState) {
        if (permissionManager.hasPermission(context)) {
            permissionManager.onPermissionGranted()
        } else {
            permissionManager.requestPermission(context, permissionLauncher)
        }
    }

    if (DEBUG){
        try {
            Log.d(
                "Composable value",
                        "ACE: $currentTime - ${currentSolarWindData!!.speed}\n" +
                        "Epam: $currentTime - ${df.format(currentImfData!!.bz)}"
            )
        } catch (e: NullPointerException) {
            Log.e("Composable value", e.toString())
        }
    }


    if(settingsVisible!!){
        SettingsScreen()
    }
    else{
        MainScreen(
    //        formatter.format(currentTime),
            dateTimeString,
            currentImfData!!.bz,
            currentSolarWindData!!.speed,
            currentSolarWindData!!.density,
            currentSolarWindData!!.temperature,
            debugString,
            df.format(currentDuration),

        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    time: String,
    bz: Double,
    speed: Double,
    density: Double,
    temp: Double,
    noaaAlerts: String,

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
                        speed,
                        density,
                        temp
                    )
                    
                    Text(text = noaaAlerts, color = Color.LightGray)
                    
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
        358.6,
        56.2,
        123.4,
        "",
        "54.2",
    )
}


