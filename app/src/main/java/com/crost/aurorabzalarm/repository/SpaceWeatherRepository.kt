package com.crost.aurorabzalarm.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crost.aurorabzalarm.data.ImfData
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.NoaaAlerts.GEO_STORM_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_WARNING_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.MAX_MINUTES_BETWEEN_ALERT_AND_NOW
import com.crost.aurorabzalarm.data.SolarWindData
import com.crost.aurorabzalarm.network.DataOperator
import com.crost.aurorabzalarm.ui.screens.settings.loadSettingsConfig
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.SpaceWeatherNotificationService
import com.crost.aurorabzalarm.utils.datetime_utils.calculateTimeDifferenceFromNow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val DEBUG_REPOSITORY = false

class SpaceWeatherRepository(application: Application) {
    private val con = application.applicationContext
    private var notificationService: SpaceWeatherNotificationService
    private var exceptionHandler = ExceptionHandler.getInstance(con)

    private val networkOperator = DataOperator(con)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val settings = loadSettingsConfig(con)


    val timeNow: LocalDateTime = LocalDateTime.now()
    private var latestKpAlertTime = LocalDateTime.now().minusHours(1L)
    private var latestKpWarningTime = LocalDateTime.now().minusHours(1L)
    private var latestSolarAlertTime = LocalDateTime.now().minusHours(1L)


    private val _latestNoaaKpAlert = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = ""
        )
    )
    private val _latestNoaaKpWarning = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = ""
        )
    )
    private val _latestNoaaSolarStormAlert = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = ""
        )
    )

    private val _latestImfData = MutableLiveData(
        ImfData(dateTime = LocalDateTime.now(), -999.9, -999.9, -999.9, -999.9)
    )

    private val _latestParticleData = MutableLiveData(
        SolarWindData(LocalDateTime.now(), -999.9, -999.9, -999.9)
    )

    val latestImfData: LiveData<ImfData> get() = _latestImfData
    val latestParticleData:LiveData<SolarWindData> get() = _latestParticleData

    val latestNoaaKpAlert:LiveData<NoaaAlert> get() = _latestNoaaKpAlert
    val latestNoaaKpWarning:LiveData<NoaaAlert> get() = _latestNoaaKpWarning
    val latestNoaaSolarStormAlert:LiveData<NoaaAlert> get() = _latestNoaaSolarStormAlert


    init {
        var retryCount = 0
        notificationService = SpaceWeatherNotificationService(con)

        scope.launch {
            fetchDataAndStore()
        }
    }


    suspend fun fetchDataAndStore() {
        var data: List<List<Any>>
        do {
            data = networkOperator.fetchData(con) //as List<Any>
            delay(500)
        } while (data.isEmpty())

        handleIncomingData(data)
    }


    //https://services.swpc.noaa.gov/products/noaa-planetary-k-index.json
    private fun handleIncomingData(
        allDataLists: List<List<Any>>,
    ) {
        for (dataList in allDataLists) {
            when (dataList.last()) {
                is ImfData -> {
                    val newImfData = dataList.last() as ImfData
                    if(newImfData.bz < latestImfData.value!!.bz   ||
                        newImfData.bt > latestImfData.value!!.bt){
                        checkIfNotificationIsNecessary(newImfData)
                    }
                    _latestImfData.postValue(newImfData)
                }

                is SolarWindData -> {
                    val newSolarWindData = dataList.last() as SolarWindData
                    _latestParticleData.postValue(newSolarWindData)
                }

                is NoaaAlert -> {
                    val newNoaaAlertList = dataList as List<NoaaAlert>
                    handleNoaaAlertNotifications(newNoaaAlertList)
                }
            }
        }
    }

    private fun handleNoaaAlertNotifications(dataList: List<NoaaAlert>) {
        for (alert in dataList) {
            val duration = calculateTimeDifferenceFromNow(alert.datetime)
            when (alert.id) {
                in KP_ALERT_IDs -> {
                    if (alert.datetime != latestKpAlertTime) {
                        latestKpAlertTime = alert.datetime

                        if (duration.toMinutes() <= MAX_MINUTES_BETWEEN_ALERT_AND_NOW) {
                            notificationService.showNoaaAlertNotification(alert)
                        }
                    }
                    _latestNoaaKpAlert.postValue(alert)
                }

                in KP_WARNING_IDs -> {
                    if (alert.datetime != latestKpWarningTime) {
                        latestKpWarningTime = alert.datetime

                        if (duration.toMinutes() <= MAX_MINUTES_BETWEEN_ALERT_AND_NOW) {
                            notificationService.showNoaaAlertNotification(alert)
                        }
                    }
                    _latestNoaaKpWarning.postValue(alert)
                }

                in GEO_STORM_ALERT_IDs -> {
                    if (alert.datetime != latestSolarAlertTime) {
                        latestSolarAlertTime = alert.datetime

                        if (duration.toMinutes() <= MAX_MINUTES_BETWEEN_ALERT_AND_NOW) {
                            notificationService.showNoaaAlertNotification(alert)
                        }
                    }
                    _latestNoaaSolarStormAlert.postValue(alert)
                }
            }
        }
    }


    private fun checkIfNotificationIsNecessary(data: Any) {
        if (settings.notificationEnabled) {
            if (data is ImfData) {
                if (data.bz <= settings.bzWarningLevel.currentValue) {
                    notificationService.showSpaceWeatherNotification(data.bz, data.bt)
                }
            } else if (data is NoaaAlert) {
                if (data.id != "0") notificationService.showNoaaAlertNotification(data)
            }
        }
    }
}
