package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.crost.aurorabzalarm.R
import java.math.RoundingMode
import java.text.DecimalFormat


// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    val currentHpVal = viewModel.latestHpState.value
    val currentAceVal = viewModel.latestAceState.value
    val currentSpeed = viewModel.latestEpamState.value
    val currentTime = viewModel.dateTimeString

    val currentDuration = viewModel.currentDurationOfFlight


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Values(
            currentTime,
            currentAceVal!!.bz,
            currentHpVal!!.hpNorth,
            currentSpeed!!.speed,
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
    currentDuration: Double
//    viewModel: DataViewModel
){
    val padding_s = dimensionResource(R.dimen.padding_small)
    val padding_m = dimensionResource(R.dimen.padding_middle)
    val padding_l = dimensionResource(R.dimen.padding_large)

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING



    try {
        Log.d("Composables value",
            "HP: $currentTime - ${currentHpVal}\n" +
                    "ACE: $currentTime - $currentAceVal"+
                    "Epam: $currentTime - ${df.format(currentSpeed)}")
    } catch (e: NullPointerException){
        Log.e("Composables value", e.toString())
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding_l)
    ) {
         Column(
            modifier = Modifier.padding(PaddingValues(padding_s)),
            horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement =Arrangement.SpaceEvenly
        ) {
            Text(currentTime)

            Text("Currently ${df.format(currentDuration)} Minutes from DISCOVR to earth")

        }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(PaddingValues())
            ) {
                Text(
                    currentHpVal.toString(),
                    modifier = Modifier
                )
                Text(
                    text = currentAceVal.toString(),
                )
                Text(currentSpeed.toString())
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
        56.2
    )
}