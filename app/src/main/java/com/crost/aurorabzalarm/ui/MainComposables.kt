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

            viewModel.fetchData()
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

    Column {
        Text(
            text = "CurrentDataState: \n" +
//                    "Bz Value:\t ${currentLiveData.value?.get("bzVal")}\n" + // does not update!
                    "Bz Value:\t ${currentLiveData.value?.weatherData?.get("bzVal")}\n" + // does not update!
                    "Hemispheric Power: ${currentLiveData.value?.get("hpVal")} GW",
            modifier = modifier,
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