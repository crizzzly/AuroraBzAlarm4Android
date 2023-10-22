package com.crost.aurorabzalarm.ui

import android.app.Application
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.repository.SpaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DataViewModel(application: Application) : AndroidViewModel(application) {
    private var spaceWeatherRepository: SpaceWeatherRepository

    private val _latestSpaceWeatherData = MutableLiveData<MutableMap<String, Any>>()
    val latestSpaceWeatherData: LiveData<MutableMap<String, Any>> get() = _latestSpaceWeatherData


//    private val _spaceWeatherState = MutableLiveData<SpaceWeatherState>()
//    val spaceWeatherState: LiveData<SpaceWeatherState> get() = _spaceWeatherState

    var outputText: String = ""

    // TODO: Create Color-Set
    var outputTextColor: Color = Color.LightGray


    init {
//        _spaceWeatherState.value = SpaceWeatherState.Loading
        spaceWeatherRepository = SpaceWeatherRepository(application)
        viewModelScope.launch(Dispatchers.IO) {
            _latestSpaceWeatherData.value = spaceWeatherRepository.getLatestData()
//            fetchSpaceWeatherData() //not necessary - should be triggered by worker every minute
        }
    }


    fun fetchSpaceWeatherData() {
        Log.i("DataViewModel", "fetchSpaceWeatherData")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                spaceWeatherRepository.fetchDataAndStore()
            } catch (e: Exception) {
                Log.e("fetchSpaceWeatherData", e.stackTraceToString())
//                SpaceWeatherState.Error(e.message ?: "Unknown error")
            }
            _latestSpaceWeatherData.value = spaceWeatherRepository.getLatestData()

            Log.i("fetch..ViewModel", "_latestSpaceWeatherData ${_latestSpaceWeatherData.value}")
            Log.i("fetch..ViewModel", "latestSpaceWeatherData ${latestSpaceWeatherData.value}")
            try {
                outputText = "CurrentDataState: \n" +
//                    "Bz Value:\t ${currentLiveData.value?.get("bzVal")}\n" + // does not update!
                        "Bz Value:\t ${latestSpaceWeatherData.value?.get("bz")} Nt\n" + // does not update!
                        "Hemispheric Power: ${latestSpaceWeatherData.value?.get("hpNorth")} GW"
            } catch (e: NoSuchElementException) {
                outputText = "Error \nProbs with accessing WeatherDataState"
                Log.e("MainComposables - Values", e.stackTraceToString())
                outputTextColor = Color.Red
            }

        }
    }
}


object ViewModelFactory {
    private lateinit var dataViewModel: DataViewModel

    fun init(application: Application) {
        Log.i("ViewModelFactory", "init")
        dataViewModel =
            ViewModelProvider.AndroidViewModelFactory(application).create(DataViewModel::class.java)
    }

    fun getDataViewModel(): DataViewModel {
        return dataViewModel
    }
}


//
//private fun updateCurrentSpaceWeatherState(newData: CurrentSpaceWeatherState){
//    Log.d("Dvm - updateCurrentSpaceWeatherState", "length: ${newData.weatherData.size} \n" +
//            newData.getAsPrintableString()
//    )
//    _currentSpaceWeatherStateFlow.value.weatherData = newData.weatherData as MutableMap<String, Any>
//
//    try {
//        outputText = "CurrentDataState: \n" +
////                    "Bz Value:\t ${currentLiveData.value?.get("bzVal")}\n" + // does not update!
//                "Bz Value:\t ${currentSpaceWeatherLiveData.value?.weatherData?.get("bzVal")}\n" + // does not update!
//                "Hemispheric Power: ${currentSpaceWeatherLiveData.value?.get("hpVal")} GW"
//    } catch (e: NoSuchElementException){
//        outputText = "Error \nProbs with accessing WeatherDataState"
//        Log.e("MainComposables - Values", e.stackTraceToString())
//        outputTextColor = Color.Red
//    }
//
//
//
//            as MutableMap<String, Any>
//
//    Log.d("Dvm - currentSpaceWeatherLiveData.value ", currentSpaceWeatherLiveData.value.toString())
//}
//
//private lateinit var _newData: MutableStateFlow<MutableMap<String, Any>>
//
//private val _currentSpaceWeatherStateFlow = MutableStateFlow(MutableCurrentSpaceWeatherState())
////    val currentSpaceWeatherState: StateFlow<CurrentSpaceWeatherState> = _currentSpaceWeatherStateFlow
//
//val currentSpaceWeatherLiveData: LiveData<CurrentSpaceWeatherState>  =
//    _currentSpaceWeatherStateFlow.asLiveData(Dispatchers.IO)
//
//
//// ------------------------------------------------------------------- //


//
//@Stable
//interface CurrentSpaceWeatherState {
//    val weatherData: Map<String, Any>
//
//    fun getAsPrintableString(): String{
//        var string = ""
//        val iter = weatherData.iterator()
//
//        iter.forEach {entry: Map.Entry<String, Any> ->
//            string += "${entry.key}: ${entry.value}\n"
//        }
//
//        return string
//    }
//
//    operator fun get(s: String): Any? {
//        return weatherData[s]
//
//    }
//}
//
//private class MutableCurrentSpaceWeatherState : CurrentSpaceWeatherState {
//    override var weatherData: MutableMap<String, Any> by mutableMapOf<String, Any>()
//}

//data class SpaceWeatherState(
//    override val weatherData: Map<String, Any>,
//) : CurrentSpaceWeatherState


//            var newData: Flow<SpaceWeatherState>
//            try {
//                 newData = spaceWeatherRepository.fetchDataAndStoreInDatabase()
//                Log.i("ViewModel - new data", newData.toString())
//            } catch (e: Exception){
//                Log.e("DataViewModel fetchData", e.stackTraceToString())
//            }
