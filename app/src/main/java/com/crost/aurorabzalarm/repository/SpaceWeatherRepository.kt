package com.crost.aurorabzalarm.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.crost.aurorabzalarm.data.getLatestAceValuesFromDb
import com.crost.aurorabzalarm.data.getLatestEpamValuesFromDb
import com.crost.aurorabzalarm.data.getLatestHpValuesFromDb
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import com.crost.aurorabzalarm.data.saveDataModelInstances
import com.crost.aurorabzalarm.network.NetworkOperator
import com.crost.aurorabzalarm.network.parser.NoaaAlert
import com.crost.aurorabzalarm.network.parser.NoaaAlertHandler
import com.crost.aurorabzalarm.settings.loadSettingsConfig
import com.crost.aurorabzalarm.utils.AuroraNotificationService
import com.crost.aurorabzalarm.utils.Constants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.DB_NAME
import com.crost.aurorabzalarm.utils.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.datetime_utils.formatTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val DEBUG_REPOSITORY = false

class SpaceWeatherRepository(application: Application) {
    private val con = application.applicationContext
    private lateinit var db: SpaceWeatherDataBase
    private var notificationService: AuroraNotificationService
    private var exceptionHandler = ExceptionHandler.getInstance(con)

    private val networkOperator = NetworkOperator(con)
    private val noaaAlertHandler = NoaaAlertHandler()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val settings = loadSettingsConfig(con)


    private var _latestHpData = MutableLiveData(
        HemisphericPowerData(0, -999, -999)
    )
    private var _latestAceData = MutableLiveData(
        AceMagnetometerData(0, -999.9, -999.9, -999.9, -999.9)
    )
    private var _latestEpamData = MutableLiveData(
        AceEpamData(0, -9999.9, -9999.9, -9999.9)
    )
    private val _latestNoaaKpAlert = MutableLiveData(
        NoaaAlert(
            id = "0", issueDatetime = LocalDateTime.now(), message = "")
        )
    private val _latestNoaaKpWarning = MutableLiveData(
        NoaaAlert(
            id = "0", issueDatetime = LocalDateTime.now(), message = "")
    )
    private val _latestNoaaSolarStormAlert = MutableLiveData(
        NoaaAlert(
            id = "0", issueDatetime = LocalDateTime.now(), message = "")
    )


    val latestHpData get() = _latestHpData
    val latestAceData get() = _latestAceData
    val latestEpamData get() = _latestEpamData
    val latestNoaaKpAlert get() = _latestNoaaKpAlert
    val latestNoaaKpWarning get() = _latestNoaaKpWarning
    val latestNoaaSolarStormAlert get() = _latestNoaaSolarStormAlert


    init {
        var retryCount = 0
        notificationService = AuroraNotificationService(con)

        do {
            try {  // db init
                if (DEBUG_REPOSITORY) Log.i("SpaceWeatherRepository", "init db")
                db = Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME)
//                        .addMigrations(migration1to2, migration1to3, migration2to3, )
//                        .fallbackToDestructiveMigration() // This line ensures any existing database will be cleared
                    .build()
                retryCount = 3
            } catch (e: RuntimeException) {
                val msg = "unable to instantiate database: \n${e.stackTraceToString()}"
                exceptionHandler.handleExceptions(
                    con, "SpaceWeatherRepository", msg)

                retryCount++
//                delay()
            } catch (e: NullPointerException) {
                val msg = "SpaceWeatherRepository Init Error: \n " + "unable to instantiate database:\n" + "${e.stackTraceToString()} "
                exceptionHandler.handleExceptions(
                    con, "SpaceWeatherRepository", msg)

                retryCount++
            }
        } while (retryCount < MAX_RETRY_COUNT)

        setAceDataCollector()
        setHpDataCollector()
        setEpamDataCollector()
        setNoaaAlertCollector()

        scope.launch {
            fetchDataAndStore()
        }
    }


    suspend fun fetchDataAndStore() {
        var data: MutableMap<String, MutableList<Any>>
        do {
            data = networkOperator.fetchData(con) as MutableMap<String, MutableList<Any>>
            delay(500)
        } while (data.isEmpty())

        handleIncomingData(con, data)
    }


    private suspend fun handleIncomingData(
        context: Context,
        allDataLists: MutableMap<String, MutableList<Any>>,
    ) {
        for (table in allDataLists.keys) {
            if (table == ALERTS_PSEUDO_TABLE_NAME) {
                noaaAlertHandler.handleAlerts(allDataLists[table] as List<NoaaAlert>)

            }
            else{
                try {
                    saveDataModelInstances(
                        context = context,
                        exceptionHandler = exceptionHandler,
                        db = db,
                        dataTable = allDataLists[table]!! as MutableList<MutableMap<String, Any>>,
                        tableName = table
                    )
                } catch (e: Exception){
                    exceptionHandler.handleExceptions(
                        con, "storeDataInDb", "Error processing data: ${e.message}"
                    )
                    throw e
                }
            }
        }
    }


    private fun setNoaaAlertCollector(){
        scope.launch {
            try {
                val latestKpAlerts = noaaAlertHandler.kpAlert as Flow<NoaaAlert>
                latestKpAlerts.collect{kpAlert ->
                    checkIfNotificationIsNecessary(kpAlert)
                    Log.d("KpAlert", kpAlert.message)
                    _latestNoaaKpAlert.postValue(kpAlert)
                }
                val latestKpWarnings = noaaAlertHandler.kpWarning as Flow<NoaaAlert>
                latestKpWarnings.collect{ kpWarning ->
                    _latestNoaaKpWarning.postValue(kpWarning)
                }
                val latestSolarStormAlert = noaaAlertHandler.geoAlert as Flow<NoaaAlert>
                latestSolarStormAlert.collect{solarAlert ->
                    _latestNoaaSolarStormAlert.postValue(solarAlert)
                }

            } catch (e: Exception){
                exceptionHandler.handleExceptions(
                    con, "setNoaaAlertCollector", e.stackTraceToString()
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setHpDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setHpDataCollector", "setHpDat Collector")

        scope.launch {
            try {
                val latestData =
                    getLatestHpValuesFromDb(exceptionHandler, con, db) as Flow<HemisphericPowerData>
                latestData.collect { hpData ->
                    checkIfNotificationIsNecessary(hpData)
                    if (DEBUG_REPOSITORY) Log.d("HpDataCollector", hpData.hpNorth.toString())
                    _latestHpData.postValue(hpData)
                }
            } catch (e: Exception) {
                exceptionHandler.handleExceptions(con, "HpDataCollector", e.stackTraceToString())
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun setAceDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setAceDataCollector", "setAceDataCollector")
        scope.launch {
            try {
                val latestData = getLatestAceValuesFromDb(
                    exceptionHandler,
                    con,
                    db
                ) as Flow<AceMagnetometerData>

                latestData.collect { aceData ->
                    checkIfNotificationIsNecessary(aceData)
                    if (DEBUG_REPOSITORY) {
                        Log.d(
                            "getLatestAceData",
                            "${formatTimestamp(aceData.datetime)}: Bz ${aceData.bz} nT"
                        )
                    }
                    _latestAceData.postValue(aceData)
                }
            } catch (e: Exception) {
                exceptionHandler.handleExceptions(con, "AceDataCollector", e.stackTraceToString())
            }
        }
    }

    private fun checkIfNotificationIsNecessary(data: Any) {
        if(settings.notificationEnabled){
            if (data is AceMagnetometerData){
                if(data.bz >= settings.bzWarningLevel.currentValue){
                    notificationService.showSpaceWeatherNotification(data.bz, 15)
                }
            }
            else if (data is NoaaAlert){
                if (data.id != "0") notificationService.showNoaaAlert(data)
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun setEpamDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setEpamDataCollector", "setEpamDataCollector")
        scope.launch {
            try {
                val latestData = getLatestEpamValuesFromDb(
                    exceptionHandler,
                    con,
                    db
                ) as Flow<AceEpamData>
                latestData.collect { epamData ->
                    checkIfNotificationIsNecessary(epamData)
                    if (DEBUG_REPOSITORY) {
                        Log.d(
                            "getLatestEpamData",
                            "${formatTimestamp(epamData.datetime)}: Speed ${epamData.speed} km/s"
                        )

                    }
                    _latestEpamData.postValue(epamData)
                }
            } catch (e: Exception) {
                exceptionHandler.handleExceptions(
                    con, "AceDataCollector", "${e.stackTraceToString()} "
                )
            }
        }
    }
}


