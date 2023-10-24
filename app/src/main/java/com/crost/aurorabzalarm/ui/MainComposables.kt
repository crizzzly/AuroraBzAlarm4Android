package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    val currentHpVal = viewModel.latestHpState.value
    val currentAceVal = viewModel.latestAceState.value
    val currentTime = viewModel.dateTimeString

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Values(
            currentTime,
            currentAceVal!!.bz,
            currentHpVal!!.hpNorth
        )

        Button(onClick = {
            Log.d("Button Clicked", "starting download and saing process")
            viewModel.fetchSpaceWeatherData()
        }) {
            Text(text = "Reload")
        }
//        if (currentDataState != null) {
//            SpeedometerScreen()
//        }
    }
}

@Composable
fun Values(
    currentTime: String,
    currentAceVal: Double,
    currentHpVal: Int,
//    viewModel: DataViewModel
){


    try {
        Log.d("Composables value",
            "HP: $currentTime - ${currentHpVal}\n" +
                    "ACE: $currentTime - $currentAceVal")
    } catch (e: NullPointerException){
        Log.e("Composables value", e.toString())
    }
    // TODO: better manage in ViewModel?
//    val outputText = viewModel.outputText
//    val outputTextColor = viewModel.outputTextColor

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(currentTime)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    currentHpVal.toString(),
                    modifier = Modifier
                )
                Text(
                    text = currentAceVal.toString(),
                )
            }
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
    )
}