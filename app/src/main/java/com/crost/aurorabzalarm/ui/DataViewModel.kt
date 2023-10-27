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
import com.crost.aurorabzalarm.network.parser.formatTimestamp
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
            hpNorth = -999,
            hpSouth = -999
        ))

    private val _latestAceState = mutableStateOf<AceMagnetometerData?>(
        AceMagnetometerData(
            datetime = 0,
            bx = -999.0,
            by = -999.0,
            bt = -999.0,
            bz = -999.0
    ))

    private val _latestEpamState = mutableStateOf<AceEpamData?>(
        AceEpamData(
            datetime = 0,
            density = -999.9,
            speed = -999.9,
            temp = -999.9
        )
    )

    val latestHpState: State<HemisphericPowerData?> get() = _latestHpState
    val latestAceState: State<AceMagnetometerData?> get() = _latestAceState
    val latestEpamState: State<AceEpamData?> get() = _latestEpamState

    val currentDurationOfFlight: Double get() = getTimeOfDataFlight(latestEpamState.value?.speed)

    val dateTimeString: String get() = formatTimestamp(_latestAceState.value!!.datetime)


    init {
        spaceWeatherRepository = SpaceWeatherRepository(application)

        Log.d("DataViewModel Init", "Observing values")
        initAceObserver()
        initEpamObserver()
        initHpObserver()

        Log.d("DataViewModel Init", "Init completed. starting first fetching process ... ")
        fetchSpaceWeatherData()
    }



    private fun initHpObserver() {
        spaceWeatherRepository.latestHpData.observeForever {
            try {
                _latestHpState.value = it
                Log.d("HpObserver", "latest hp: ${it.hpNorth} GW")
            } catch (e: Exception){
                Log.e("HpObserver", "HpObserver: ${e.stackTraceToString()}")
            }
        }
    }

    private fun initEpamObserver() {
        spaceWeatherRepository.latestEpamData.observeForever {
            try {
                _latestEpamState.value = it
                Log.d("EpamObserver", "LatestSpeedVal: ${it.speed} km/s")
            } catch (e: Exception){
                Log.e("EpamObserver", "EpamObserver: ${e.stackTraceToString()}")
            }
        }
    }

    private fun initAceObserver(){
        spaceWeatherRepository.latestAceData.observeForever {
            try {
                _latestAceState.value = it
                Log.d("AceObserver", "initialized. latest: ${it.bz}")
            } catch (e: Exception){
                Log.e("AceObserver", "AceObserver: ${e.stackTraceToString()}")
            }
        }
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


    private fun getTimeOfDataFlight(speed: Double?): Double {
        val exampleSpeed = 377.2 // 1,357e+6 km/h
        val distance = 1500000.0
        val timeInS = distance/speed!!
        val timeInM = timeInS/60
        Log.d("getTimeOfDataFlight", "distance: $distance, speed:$speed, time: $timeInM")
        return timeInM

    }


    private fun formatTiimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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
