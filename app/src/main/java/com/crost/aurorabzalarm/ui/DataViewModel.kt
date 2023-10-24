package com.crost.aurorabzalarm.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import com.crost.aurorabzalarm.repository.SpaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class DataViewModel(application: Application) : AndroidViewModel(application) {
    private var spaceWeatherRepository: SpaceWeatherRepository

    // TODO: Create Color-Set
    var outputTextColor: Color = Color.LightGray


    private val _latestHpState = mutableStateOf<HemisphericPowerData?>(
        HemisphericPowerData(
            datetime = 0,
            hpNorth = 0,
            hpSouth = 0
        ))
    val latestHpState: State<HemisphericPowerData?> get() = _latestHpState

    private val _latestAceState = mutableStateOf<AceMagnetometerData?>(
        AceMagnetometerData(
            datetime = 0,
            bx = -999.9,
            by = -999.9,
            bt = -999.9,
            bz = -999.9
    ))
    val latestAceState: State<AceMagnetometerData?> get() = _latestAceState

    private val _latestEpamState = mutableStateOf<AceEpamData?>(
        AceEpamData(
            datetime = 0,
            density = 0.0,
            speed = 0.0,
            temp = 0.0
        )
    )
    val latestEpamState: State<AceEpamData?> get() = _latestEpamState

    val currentDurationOfFlight: Double get() = getTimeOfDataFlight(latestEpamState.value?.speed)

    private fun getTimeOfDataFlight(speed: Double?): Double {
        val exampleSpeed = 377.2 // 1,357e+6 km/h
        val distance = 1500000.0
        val timeInS = distance/speed!!
        return timeInS/60

    }

    val dateTimeString: String get() = formatTimestamp(_latestAceState.value!!.datetime)

    // calculate time the storm needs to get from DISCOVR to earth
//    val timeOfFlight -> first implement epam parsing logic!



    init {
        spaceWeatherRepository = SpaceWeatherRepository(application)



        Log.d("DataViewModel Init", "Observing values")
        spaceWeatherRepository.latestAceValue.observeForever {
            try {
                _latestAceState.value = it
            } catch (e: Exception){
                Log.e("DataViewModel init", "AceObserver: ${e.stackTraceToString()}")
            }
        }

        spaceWeatherRepository.latestHpValue.observeForever {
            try {
                _latestHpState.value = it
            } catch (e: Exception){
                Log.e("DataViewModel init", "HpObserver: ${e.stackTraceToString()}")
            }
        }

        Log.d("DataViewModel Init", "Init completed. starting first fetching process ... ")
        fetchSpaceWeatherData()
    }


    fun fetchSpaceWeatherData() {
        Log.i("DataViewModel", "fetchSpaceWeatherData")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                spaceWeatherRepository.fetchDataAndStore()
//                async { spaceWeatherRepository.fetch/**/DataAndStore() }
            } catch (e: Exception) {/**/
                Log.e("fetchSpaceWeatherData", e.stackTraceToString())
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // You can customize the format here
        return formatter.format(localDateTime)
    }

}


object ViewModelFactory {
    private lateinit var dataViewModel: DataViewModel

    fun init(application: Application) {
        Log.i("ViewModelFactory", "init")
        dataViewModel =
            ViewModelProvider.AndroidViewModelFactory(application)
                .create(DataViewModel::class.java)
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
