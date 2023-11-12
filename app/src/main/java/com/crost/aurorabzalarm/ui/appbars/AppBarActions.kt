package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import com.crost.aurorabzalarm.viewmodels.DataViewModel

@Composable
fun AppBarActions(){
    val settingsViewModel: SettingsViewModel = viewModel()
    val dataViewModel: DataViewModel = viewModel()
    val con = LocalContext.current
    IconButton(onClick =  {
        dataViewModel.setLogfileVisible(true)
    }){
        Icon(
            imageVector = Icons.Sharp.PlayArrow,
            contentDescription = "show logfile"
        )
    }
    IconButton(
        onClick = {
            settingsViewModel.setSettingsVisible(true)
        },
    ) {
        Icon(
            imageVector = Icons.Default.Build, //Notifications
            contentDescription = "open alarm settings"
        )
    }
    IconButton(
        onClick = {
            dataViewModel.fetchSpaceWeatherData(con)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "reload spaceWeatherData"
        )
    }
}