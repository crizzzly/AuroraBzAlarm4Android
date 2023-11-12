package com.crost.aurorabzalarm.ui

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
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

private const val DEBUG = false

// TODO: SnackBarHost in Scaffold
// TODO: show some graphs/animations from noaa like WSA_ENLIL

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    if (! permissionState) {
        if (permissionManager.hasPermission(context)) {
            permissionManager.onPermissionGranted()
        } else {
            permissionManager.requestPermission(context, permissionLauncher)
        }
    }



    if(settingsVisible){
        SettingsScreen()
    }
    else if(showLogs){
        LogFileScreen()
    }
    else{
        MainScreen()
    }
}
