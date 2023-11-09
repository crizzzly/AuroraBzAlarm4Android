package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.R
import com.crost.aurorabzalarm.settings.SettingsViewModel
import com.crost.aurorabzalarm.viewmodels.DataViewModel

//import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory

@Preview//(uiMode = Configuration.UI_MODE_NIGHT_MASK)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuroraAppBar(){
    val con = LocalContext.current
    val dataViewModel: DataViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {

            Text(stringResource(R.string.app_name))
        },
        actions = {
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
//                    loadNewestData(con)
                    dataViewModel.fetchSpaceWeatherData()
                })
            {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "reload spaceWeatherData"
                )
            }
        }
    )
}

//fun loadNewestData(context: Context) {
//    val viewModel = AuroraViewModelFactory.getDataViewModel()
//    viewModel.fetchSpaceWeatherData()
//    Toast.makeText(context, "New SpaceWeatherData", Toast.LENGTH_LONG).show()
//}
//
//fun setAlarmSettingsVisible(){
//    val viewModel = AuroraViewModelFactory.getSettingsViewModel()
//    viewModel.setSettingsVisible(true)
//}