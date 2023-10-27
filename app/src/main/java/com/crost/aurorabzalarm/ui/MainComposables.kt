package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.crost.aurorabzalarm.R
import com.crost.aurorabzalarm.ui.elements.PreviewAllCharts
import java.math.RoundingMode
import java.text.DecimalFormat


// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    val currentHpVals by remember { viewModel.latestHpState }
    val currentAceVals by remember { viewModel.latestAceState }
    val currentEpamVals by remember { viewModel.latestEpamState }
    val currentTime = viewModel.dateTimeString

    val currentDuration = viewModel.currentDurationOfFlight


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Values(
            currentTime,
            currentAceVals!!.bz,
            currentHpVals!!.hpNorth ,
            currentEpamVals!!.speed,
            currentEpamVals!!.density,
            currentEpamVals!!.temp,
            currentDuration
        )

        Button(onClick = {
            Log.d("Button Clicked", "starting download and saing process")
            viewModel.fetchSpaceWeatherData()
        }) {
            Text(text = "Reload")
        }

    }
}

@Composable
fun Values(
    currentTime: String,
    currentAceVal: Double,
    currentHpVal: Int,
    currentSpeed: Double,
    currentDensity: Double,
    currentTemp: Double,
    currentDuration: Double
) {
    val padding_s = dimensionResource(R.dimen.padding_small)
    val padding_m = dimensionResource(R.dimen.padding_middle)
    val padding_l = dimensionResource(R.dimen.padding_large)

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    val scrollState = rememberScrollState()



    try {
        Log.d(
            "Composables value",
            "HP: $currentTime - ${currentHpVal}\n" +
                    "ACE: $currentTime - $currentAceVal\n" +
                    "Epam: $currentTime - ${df.format(currentSpeed)}"
        )
    } catch (e: NullPointerException) {
        Log.e("Composables value", e.toString())
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding_l)
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(padding_l)
            ) {
                Text(currentTime)
                Text("Currently ${df.format(currentDuration)} Minutes from DISCOVR to earth")


                PreviewAllCharts(
                    currentAceVal,
                    currentHpVal.toDouble(),
                    currentSpeed,
                    currentDensity,
                    currentTemp
                )
            }
    }
}

    



@Preview(showBackground = true)
@Composable
fun ValuesPreview() {
//    val LocalApplication = staticCompositionLocalOf<Application> {
//        error("No Application provided")
//    }
//    val application = LocalContext.current.applicationContext as Application
//    CompositionLocalProvider(LocalApplication provides application) {
//        ViewModelFactory.init(application)
//    }
//    val vm = ViewModelFactory.getDataViewModel()
    Values(
        "22.10.23, 2:22",
        -15.5,
        25,
        358.6,
        56.2,
        123.4,
        54.2
    )
}