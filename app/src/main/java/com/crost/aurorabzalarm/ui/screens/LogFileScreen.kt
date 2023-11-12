package com.crost.aurorabzalarm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.ui.appbars.LogFileAppBar
import com.crost.aurorabzalarm.utils.Constants.PADDING_L
import com.crost.aurorabzalarm.utils.FileLogger


@Composable
fun LogFileScreen(){
    val con = LocalContext.current
    val fileLogger = FileLogger.getInstance(con)
    val logText = fileLogger.getLogFileContent(con)
    val textAsLines = logText//.subSequence (0, 17)
//    Log.d("LogFileScreen", "linesLength: ${textAsLines.length}")

    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            LogFileAppBar()
        }
    ) { paddingVals ->
        Surface(
            modifier = Modifier.fillMaxSize()
//            .scrollable()
        ) {
            val textHeight = (paddingVals.calculateBottomPadding()/3*2)
            Column(
                modifier = Modifier
                    .padding(PADDING_L.dp)
//                    .height(textHeight)
//                    .verticalScroll(scrollState)
            ) {
                val distFromLeft = paddingVals.calculateLeftPadding(LayoutDirection.Ltr)

//                items(textAsLines.){
                    Text(
                        text = logText,
                        Modifier
    //                        .fillMaxWidth(.95f)
                            .height(textHeight)
    //                        .width(300.dp)
                            .padding(start = distFromLeft)
//                            .verticalScroll(scrollState)
                    )

            }
        }
    }
}
