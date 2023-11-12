package com.crost.aurorabzalarm.ui

import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.screens.LogFileScreen
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import com.crost.aurorabzalarm.ui.screens.settings.uielements.SettingsScreen
import com.crost.aurorabzalarm.utils.PermissionManager
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

const val DEBUG = true

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
    val settingsVisible by settingsViewModel.showSettings.observeAsState(
        initial = false
    )
    val showLogs by remember { dataViewModel.showLogs }

    val currentSolarWindData by remember { dataViewModel.latestSolarWindData }
    val currentImfData by remember { dataViewModel.latestImfData }
    val currentKpAlert by remember { dataViewModel.kpAlertState }
    val currentKpWarning by remember { dataViewModel.kpWarningState }
    val currentSolarStormAlert by remember { dataViewModel.solarStormState }

    val currentTime = dataViewModel.dateTime
    val dateTimeString = dataViewModel.dateTimeString
    val currentDuration = dataViewModel.currentDurationOfFlight
    
    val debugString = "Current Vals: bz: ${currentImfData.bz}, " +
            "speed: ${currentSolarWindData.speed},\n" +
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
                        "ACE: $currentTime - ${currentSolarWindData.speed}\n" +
                        "Epam: $currentTime - ${df.format(currentImfData.bz)}"
            )
            Log.d(
                "Composable alert",
                "${currentKpAlert.id}, ${currentKpWarning.id}, ${currentSolarStormAlert.id}")
        } catch (e: NullPointerException) {
            Log.e("Composable value", e.toString())
        }
    }



    if(settingsVisible){
        SettingsScreen()
    }
    else if(showLogs){
        LogFileScreen()
    }
    else{
        MainScreen(
            dateTimeString,
            currentImfData.bz,
            currentImfData.bt,
            currentSolarWindData.speed,
            currentSolarWindData.density,
            currentSolarWindData.temperature,
            debugString,
            df.format(currentDuration),
        )
    }
}
