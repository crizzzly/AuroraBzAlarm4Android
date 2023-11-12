package com.crost.aurorabzalarm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.ui.appbars.LogFileAppBar
import com.crost.aurorabzalarm.utils.Constants
import com.crost.aurorabzalarm.utils.FileLogger


@Composable
fun LogFileScreen(){
    val con = LocalContext.current
    val fileLogger = FileLogger.getInstance(con)
    val logText = fileLogger.getLogFileContent(con)
    Scaffold(
        topBar = {
            LogFileAppBar()
        }
    ) {paddingVals ->
        Surface(
            modifier = Modifier.fillMaxWidth()
//            .scrollable()
        ) {
            Column(
                modifier = Modifier.padding(Constants.PADDING_S.dp)
            ) {
                val distFromLeft = paddingVals.calculateLeftPadding(LayoutDirection.Ltr)
                Text(
                    text = logText,
                    Modifier
                        .fillMaxSize(.95f)
                        .padding(start = distFromLeft)
                )
            }
        }
    }
}
