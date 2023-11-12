package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crost.aurorabzalarm.utils.Constants.PADDING_S
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.viewmodels.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogFileAppBar(){
    val dataViewModel: DataViewModel = viewModel()
    val con = LocalContext.current

    TopAppBar(
        title = { Text(text = "Logfile") },
        navigationIcon = {
                         IconButton(
                             onClick = {
                                 dataViewModel.setLogfileVisible(false)
                             }) {
                             Icon(
                                 imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                 contentDescription ="back to mainScreen")
                         }
        },
        actions = { AppBarActions() },

    )


}
