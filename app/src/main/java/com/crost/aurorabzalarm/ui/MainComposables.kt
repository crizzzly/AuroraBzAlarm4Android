package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Values(viewModel)
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
fun Values(viewModel: DataViewModel, modifier: Modifier = Modifier){
    val currentHpVal = viewModel.latestHpState.value
    val currentAceVal = viewModel.latestAceState.value

    try {
        Log.d("Composables value",
            "HP: ${currentHpVal?.datetime} - ${ currentHpVal?.hpNorth}\n" +
                    "ACE: ${currentAceVal?.datetime} - ${currentAceVal?.bz}")
    } catch (e: NullPointerException){
        Log.e("Composables value", e.toString())
    }
    // TODO: better manage in ViewModel?
//    val outputText = viewModel.outputText
    val outputTextColor = viewModel.outputTextColor



    Text(
        text = "${currentAceVal?.bz}, ${currentHpVal?.hpNorth}",
        modifier = modifier,
        color = outputTextColor
    )
//    }
}




//@Preview(showBackground = true)
//@Composable
//fun ValuesPreview() {
//    AuroraBzAlarmTheme {
////        Values()
//    }
//}