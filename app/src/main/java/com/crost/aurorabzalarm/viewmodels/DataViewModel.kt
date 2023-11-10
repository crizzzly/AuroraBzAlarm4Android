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
import com.crost.aurorabzalarm.network.parser.NoaaAlert
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

    private val _kpAlertState = mutableStateOf(
        NoaaAlert(
            "0", LocalDateTime.now(), ""
        )
    )
    private val _kpWarningState = mutableStateOf(
        NoaaAlert(
            "0", LocalDateTime.now(), ""
        )
    )
    private val _solarStormState = mutableStateOf(
        NoaaAlert(
            "0", LocalDateTime.now(), ""
        )
    )


    val latestHpState: State<HemisphericPowerData?> get() = _latestHpState
    val latestAceState: State<AceMagnetometerData?> get() = _latestAceState
    val latestEpamState: State<AceEpamData?> get() = _latestEpamState
    val kpAlertState: State<NoaaAlert> get() = _kpAlertState
    val kpWarningState: State<NoaaAlert> get() = _kpWarningState
    val solarStormState: State<NoaaAlert> get() = _solarStormState

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



