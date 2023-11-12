package com.crost.aurorabzalarm.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.crost.aurorabzalarm.data.ImfData
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.NoaaAlertDataHandler
import com.crost.aurorabzalarm.data.NoaaAlerts.GEO_STORM_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_WARNING_IDs
import com.crost.aurorabzalarm.data.SolarWindData
import com.crost.aurorabzalarm.network.DataOperator
import com.crost.aurorabzalarm.settings.loadSettingsConfig
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.SpaceWeatherNotificationService
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
    private val noaaAlertHandler = NoaaAlertDataHandler()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val settings = loadSettingsConfig(con)


    val timeNow: LocalDateTime = LocalDateTime.now()


    private val _latestNoaaKpAlert = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = "")
        )
    private val _latestNoaaKpWarning = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = "")
    )
    private val _latestNoaaSolarStormAlert = MutableLiveData(
        NoaaAlert(
            id = "0", datetime = LocalDateTime.now(), message = "")
    )

    private val _latestImfData = MutableLiveData(
        ImfData(dateTime = LocalDateTime.now(), -999.9, -999.9 , -999.9, -999.9 )
    )

    private val _latestParticleData = MutableLiveData(
        SolarWindData(LocalDateTime.now(), -999.9, -999.9, -999.9)
    )

    val latestImfData get() = _latestImfData
    val latestParticleData get() = _latestParticleData


//    val latestAceData get() = _latestAceData
//    val latestEpamData get() = _latestEpamData
    val latestNoaaKpAlert get() = _latestNoaaKpAlert
    val latestNoaaKpWarning get() = _latestNoaaKpWarning
    val latestNoaaSolarStormAlert get() = _latestNoaaSolarStormAlert


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
            if (dataList.last() is ImfData) {
                val newImfData = dataList.last() as ImfData
                _latestImfData.postValue(newImfData)
                checkIfNotificationIsNecessary(newImfData)
            }
            else if(dataList.last() is SolarWindData){
                val newSolarWindData = dataList.last() as SolarWindData
                _latestParticleData.postValue(newSolarWindData)
            }
            else if (dataList.last() is NoaaAlert){
                val newNoaaAlertList = dataList as List<NoaaAlert>
                handleNoaaAlertsList(newNoaaAlertList)

            }
        }
    }

    private fun handleNoaaAlertsList(dataList: List<NoaaAlert>){
        var gotKpAlert = false
        var gotKpWarning = false
        var gotSolarStormAlert =false

       for (alert in dataList){
           if (!gotKpAlert && alert.id in KP_ALERT_IDs){
               if (latestNoaaKpAlert.value!!.datetime != alert.datetime){
                   notificationService.showNoaaAlert(alert)
               }
               latestNoaaKpAlert.postValue(alert)
               gotKpAlert = true
           }
           if(!gotKpWarning && alert.id in KP_WARNING_IDs){
               if (latestNoaaKpWarning.value!!.datetime != alert.datetime){
                   notificationService.showNoaaAlert(alert)
               }
               latestNoaaKpWarning.postValue(alert)
               gotKpWarning = true
           }
           if (!gotSolarStormAlert && alert.id in GEO_STORM_ALERT_IDs){
               if (latestNoaaSolarStormAlert.value!!.datetime != alert.datetime){
                   notificationService.showNoaaAlert(alert)
               }
               latestNoaaSolarStormAlert.postValue(alert)
               gotSolarStormAlert = true
           }

       }

    }


    private fun checkIfNotificationIsNecessary(data: Any) {
        if(settings.notificationEnabled){
            if (data is ImfData){
                if(data.bz <= settings.bzWarningLevel.currentValue){
                    notificationService.showSpaceWeatherNotification(data.bz, 15)
                }
            }
            else if (data is NoaaAlert){
                if (data.id != "0") notificationService.showNoaaAlert(data)
            }
        }
    }
}
