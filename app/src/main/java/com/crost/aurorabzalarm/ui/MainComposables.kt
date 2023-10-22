package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_COL_DT

// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
    Log.d("MainComposable VM", viewModel.toString())
    Column {
        Values(viewModel)
        Button(onClick = {
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
    val currentLiveData = viewModel.latestSpaceWeatherData.observeAsState()

    try {
        Log.d("Composables value",
            "${ currentLiveData.value!![ACE_COL_DT]}\n${ currentLiveData.value!![ACE_COL_BZ]}")
    } catch (e: NullPointerException){
        Log.e("Composables value", e.toString())
    }
    // TODO: better manage in ViewModel?
    val outputText = viewModel.outputText
    val outputTextColor = viewModel.outputTextColor


    Column {
        Text(
            text = outputText,
            modifier = modifier,
            color = outputTextColor
        )
    }
}




//@Preview(showBackground = true)
//@Composable
//fun ValuesPreview() {
//    AuroraBzAlarmTheme {
////        Values()
//    }
//}