package com.crost.aurorabzalarm.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crost.aurorabzalarm.ui.appbars.AuroraAppBar
import com.crost.aurorabzalarm.ui.panels.PreviewAllPanels
import com.crost.aurorabzalarm.utils.Constants


@Composable
fun MainScreen(
    time: String,
    bz: Double,
    bt: Double,
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
                        .padding(0.dp, Constants.PADDING_L.dp)
                ) {
                    Text(
                        time,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, Constants.PADDING_S.dp)
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
        23.5,
        358.6,
        56.2,
        123.4,
        "",
        "54.2",
    )
}
