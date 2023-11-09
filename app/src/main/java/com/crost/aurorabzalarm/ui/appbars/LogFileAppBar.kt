package com.crost.aurorabzalarm.ui.appbars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.crost.aurorabzalarm.utils.FileLogger

@Composable
fun LogFileAppBar(){
    val con = LocalContext.current
    val fileLogger = FileLogger.getInstance(con)
//    val log = fileLogger.getLogFileContent(con)



}

@Composable
fun LogFileContent(){
    val con = LocalContext.current
    val fileLogger = FileLogger.getInstance(con)
    val logText = fileLogger.getLogFileContent(con)

    Surface(
        modifier = Modifier.fillMaxWidth()
//            .scrollable()
    ) {
        Column {
            Text(
                text = logText,
                Modifier.fillMaxSize(.95f)
            )
        }

    }
}