package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import com.crost.aurorabzalarm.viewmodels.DataViewModel


@Preview//(uiMode = Configuration.UI_MODE_NIGHT_MASK)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuroraAppBar(){
    val dataViewModel: DataViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val con = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {

            Text(stringResource(R.string.app_name))
        },
        actions = {
            AppBarActions()
        }
    )
}
