package com.crost.aurorabzalarm.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crost.aurorabzalarm.data.ImfData
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.SolarWindData
import com.crost.aurorabzalarm.repository.SpaceWeatherRepository
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.datetime_utils.formatTimestamp
import com.crost.aurorabzalarm.utils.datetime_utils.getTimeOfDataFlight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val DEBUG_OBSERVER = true


class DataViewModel(application: Application) : AndroidViewModel(application) {
    private  var fileLogger = FileLogger.getInstance(application.applicationContext)
    private var spaceWeatherRepository: SpaceWeatherRepository
    private val exceptionHandler = ExceptionHandler.getInstance(application.applicationContext)

    private val timeNow = LocalDateTime.now()
    // TODO: Create Color-Set
    var colorOnSatDataError: Color = Color.LightGray

    private val _alarmIsEnabled = mutableStateOf(false)
    val alarmIsEnabled = _alarmIsEnabled

    private val _alarmSettingsVisible = mutableStateOf(false)
    val alarmSettingsVisible = _alarmSettingsVisible
    

    private val _latestImfData = mutableStateOf(
        ImfData(
            dateTime = timeNow,
            bx = -999.0,
            by = -999.0,
            bt = -999.0,
            bz = -999.0
        )
    )

    private val _latestSolarWindData = mutableStateOf(
        SolarWindData(
            dateTime = timeNow,
            density = -999.0,
            speed = -999.0,
            temperature = -999.0
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


    val latestSolarWindData: State<SolarWindData?> get() = _latestSolarWindData
    val latestImfData: State<ImfData?> get() = _latestImfData
    val kpAlertState: State<NoaaAlert> get() = _kpAlertState
    val kpWarningState: State<NoaaAlert> get() = _kpWarningState
    val solarStormState: State<NoaaAlert> get() = _solarStormState

    val currentDurationOfFlight: Float get() = getTimeOfDataFlight(latestSolarWindData.value?.speed)

    val dateTimeString: String get() = formatTimestamp(_latestSolarWindData.value.dateTime)
    
    val dateTime: LocalDateTime = LocalDateTime.now()


    init {
        spaceWeatherRepository = SpaceWeatherRepository(application)

//        Log.d("DataViewModel Init", "Observing values")
        initParticleDataObserver(application.applicationContext)
        initImfDataObserver(application.applicationContext)
        initNoaaKpAlertObserver(application.applicationContext)

        fetchSpaceWeatherData(application.applicationContext)
    }


    private fun initImfDataObserver(applicationContext: Context) {
        spaceWeatherRepository.latestImfData.observeForever {
            try {
                _latestImfData.value = it
                if(DEBUG_OBSERVER) Log.d("EpamObserver", "LatestSpeedVal: ${it.bz} nT")
            } catch (e: Exception){
                exceptionHandler.handleExceptions(
                    applicationContext, "EpamObserver", "EpamObserver: ${e.stackTraceToString()}"
                )
            }
        }
    }

    private fun initParticleDataObserver(applicationContext: Context) {
        spaceWeatherRepository.latestParticleData.observeForever {
            try {
                _latestSolarWindData.value = it
                if(DEBUG_OBSERVER) Log.d("AceObserver", "initialized. latest: ${it.speed}")
            } catch (e: Exception){
                exceptionHandler.handleExceptions(applicationContext, "AceObserver", "AceObserver: ${e.stackTraceToString()}")
            }
        }
    }

    private fun initNoaaKpAlertObserver(applicationContext: Context){
        spaceWeatherRepository.latestNoaaKpAlert.observeForever{
            try {
                _kpAlertState.value = it
                if(DEBUG_OBSERVER) Log.d("initNoaaKpAlertObserver", "initialized. latest: ${it.message}")
            } catch (e:Exception){
                exceptionHandler.handleExceptions(
                    applicationContext, "initNoaaKpAlertObserver", e.stackTraceToString()
                )
            }
        }
    }

    private fun initNoaaKpWarningObserver(applicationContext: Context){
        spaceWeatherRepository.latestNoaaKpWarning.observeForever{
            try {
                _kpWarningState.value = it
                if(DEBUG_OBSERVER) Log.d("initNoaaKpWarningObserver", "initialized. latest: ${it.message}")
            } catch (e:Exception){
                exceptionHandler.handleExceptions(
                    applicationContext, "initNoaaKpWarningObserver", e.stackTraceToString()
                )
            }
        }
    }

    private fun initNoaaSolarStormAlertObserver(applicationContext: Context){
        spaceWeatherRepository.latestNoaaSolarStormAlert.observeForever{
            try {
                _solarStormState.value = it
                if(DEBUG_OBSERVER) Log.d("initNoaaSolarStormAlertObserver", "initialized. latest: ${it.message}")
            } catch (e:Exception){
                exceptionHandler.handleExceptions(
                    applicationContext, "initNoaaSolarStormAlertObserver", e.stackTraceToString()
                )
            }
        }
    }

    fun fetchSpaceWeatherData(applicationContext: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                spaceWeatherRepository.fetchDataAndStore()
            } catch (e: Exception) {
                exceptionHandler.handleExceptions(
                    applicationContext, "fetchSpaceWeatherData", e.stackTraceToString()
                )

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



