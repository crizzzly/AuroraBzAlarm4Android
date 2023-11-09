package com.crost.aurorabzalarm.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import com.crost.aurorabzalarm.repository.SpaceWeatherRepository
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.datetime_utils.formatTimestamp
import com.crost.aurorabzalarm.utils.datetime_utils.getTimeOfDataFlight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class DataViewModel(application: Application) : AndroidViewModel(application) {
    private  var fileLogger = FileLogger.getInstance(application.applicationContext)
    private var spaceWeatherRepository: SpaceWeatherRepository

    // TODO: Create Color-Set
    var colorOnSatDataError: Color = Color.LightGray

    private val _alarmIsEnabled = mutableStateOf(false)
    val alarmIsEnabled = _alarmIsEnabled

    private val _alarmSettingsVisible = mutableStateOf(false)
    val alarmSettingsVisible = _alarmSettingsVisible


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

    val currentDurationOfFlight: Float get() = getTimeOfDataFlight(latestEpamState.value?.speed)

    val dateTimeString: String get() = formatTimestamp(_latestAceState.value!!.datetime)
    
    val datetime: LocalDateTime = LocalDateTime.now()


    init {
        spaceWeatherRepository = SpaceWeatherRepository(application)

//        Log.d("DataViewModel Init", "Observing values")
        initAceObserver(application.applicationContext)
        initEpamObserver(application.applicationContext)
        initHpObserver(application.applicationContext)

        fetchSpaceWeatherData(application.applicationContext)
    }

    private fun initHpObserver(applicationContext: Context) {
        spaceWeatherRepository.latestHpData.observeForever {
            try {
                _latestHpState.value = it
//                Log.d("HpObserver", "latest hp: ${it.hpNorth} GW")
            } catch (e: Exception){
                fileLogger.writeLogsToInternalStorage(
                    applicationContext,
                    "HpObserver\n${e.stackTraceToString()}"
                )
                Log.e("HpObserver", "HpObserver: ${e.stackTraceToString()}")
            }
        }
    }

    private fun initEpamObserver(applicationContext: Context) {
        spaceWeatherRepository.latestEpamData.observeForever {
            try {
                _latestEpamState.value = it
//                Log.d("EpamObserver", "LatestSpeedVal: ${it.speed} km/s")
            } catch (e: Exception){
                fileLogger.writeLogsToInternalStorage(
                    applicationContext,
                    "EpamObserver\n${e.stackTraceToString()}"
                )
                Log.e("EpamObserver", "EpamObserver: ${e.stackTraceToString()}")
            }
        }
    }

    private fun initAceObserver(applicationContext: Context) {
        spaceWeatherRepository.latestAceData.observeForever {
            try {
                _latestAceState.value = it
//                Log.d("AceObserver", "initialized. latest: ${it.bz}")
            } catch (e: Exception){
                fileLogger.writeLogsToInternalStorage(
                    applicationContext,
                    "AceObserver\n${e.stackTraceToString()}"
                )
                Log.e("AceObserver", "AceObserver: ${e.stackTraceToString()}")
            }
        }
    }


    fun fetchSpaceWeatherData(applicationContext: Context) {
//        Log.i("DataViewModel", "fetchSpaceWeatherData")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                spaceWeatherRepository.fetchDataAndStore(applicationContext)
//                async { spaceWeatherRepository.fetch/**/DataAndStore() }
            } catch (e: Exception) {/**/
                fileLogger.writeLogsToInternalStorage(
                    applicationContext,
                    "fetchSpaceWeatherData\n${e.stackTraceToString()}"
                )
                Log.e("fetchSpaceWeatherData", e.stackTraceToString())

                // TODO: user output

            }
        }
    }


    fun setAlarmSettingsVisible(){
        _alarmSettingsVisible.value = true
    }

    fun setAlarmSettingsInvisible(){
        _alarmSettingsVisible.value = false
    }

    fun setAuroraAlarm(alarmEnabled: Boolean){
        _alarmIsEnabled.value = alarmEnabled
//        Log.d("setAuroraAlarm", "alarm enabled: $alarmEnabled")
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