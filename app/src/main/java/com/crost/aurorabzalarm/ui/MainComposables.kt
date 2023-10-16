package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier

// TODO: read about state hoisting: https://developer.android.com/codelabs/jetpack-compose-state#8
@Composable
fun MainComposable(viewModel: DataViewModel) {
//    val bzData = viewModel.currentSpaceWeatherLiveData.observeAsState().value?.bzVal?.toInt()
//    val con = LocalContext.current
    Log.d("MainComposable VM", viewModel.toString())
    Column {
        Values(viewModel)
        Button(onClick = {
                //TODO
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
//    val data: MutableMap<String, Any>? = when (val currentState = currentLiveData.value) {
//        is SpaceWeatherState.Success -> currentState.data as? MutableMap<String, Any>
//        else -> null
//    }
//    var vmodel: DataViewModel by activityViewModels()
    try {
        Log.d("Composables value",
            "${ currentLiveData.value!!.keys}\n${ currentLiveData.value!!.values}")
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