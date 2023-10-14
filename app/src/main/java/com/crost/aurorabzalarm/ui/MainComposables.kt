package com.crost.aurorabzalarm.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.crost.aurorabzalarm.ui.theme.AuroraBzAlarmTheme


@Composable
fun MainComposable(viewModel: DataViewModel) {
//    val bzData = viewModel.currentSpaceWeatherLiveData.observeAsState().value?.bzVal?.toInt()
//    val con = LocalContext.current
    Log.d("MainComposable viewModel", viewModel.toString())
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
    val currentLiveData = viewModel.currentSpaceWeatherLiveData.observeAsState()
//    var vmodel: DataViewModel by activityViewModels()
    Log.d("Composables value.toString()", currentLiveData.value.toString())
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




@Preview(showBackground = true)
@Composable
fun ValuesPreview() {
    AuroraBzAlarmTheme {
//        Values()
    }
}