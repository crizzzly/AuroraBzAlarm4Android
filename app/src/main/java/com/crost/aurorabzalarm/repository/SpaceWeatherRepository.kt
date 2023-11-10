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
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.datetime_utils.formatTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val DEBUG_REPOSITORY = false

class SpaceWeatherRepository(application: Application) {
    private lateinit var db: SpaceWeatherDataBase
    private lateinit var notificationService: AuroraNotificationService

    private val fileLogger = FileLogger.getInstance(application.applicationContext)
    private val networkOperator = NetworkOperator(application.applicationContext)
    private val noaaAlertHandler = NoaaAlertHandler()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val settings = loadSettingsConfig(application.applicationContext)


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
        notificationService = AuroraNotificationService(application.applicationContext)

        do {
            try {  // db init
                if (DEBUG_REPOSITORY) Log.i("SpaceWeatherRepository", "init db")
                db = Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME)
//                        .addMigrations(migration1to2, migration1to3, migration2to3, )
//                        .fallbackToDestructiveMigration() // This line ensures any existing database will be cleared
                    .build()
                retryCount = 3
            } catch (e: RuntimeException) {
                fileLogger.writeLogsToInternalStorage(
                    application.applicationContext,
                    "SpaceWeatherRepository Init Error: \n " + "unable to instantiate database:\n" + "${e.stackTraceToString()} "
                )
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount++
//                delay()
            } catch (e: NullPointerException) {
                fileLogger.writeLogsToInternalStorage(
                    application.applicationContext,
                    "SpaceWeatherRepository Init Error: \n " + "unable to instantiate database:\n" + "${e.stackTraceToString()} "
                )
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount++
            }
        } while (retryCount < MAX_RETRY_COUNT)

        setAceDataCollector(application.applicationContext)
        setHpDataCollector(application.applicationContext)
        setEpamDataCollector(application.applicationContext)
        setNoaaAlertCollector(application.applicationContext)

        scope.launch {
            fetchDataAndStore(application.applicationContext)
        }
    }


    suspend fun fetchDataAndStore(context: Context) {
        var data: MutableMap<String, MutableList<Any>>
        do {
            data = networkOperator.fetchData(context) as MutableMap<String, MutableList<Any>>
            delay(500)
        } while (data.isEmpty())

        handleIncomingData(context, data)
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
                        fileLogger = fileLogger,
                        db = db,
                        dataTable = allDataLists[table]!! as MutableList<MutableMap<String, Any>>,
                        tableName = table
                    )
                } catch (e: Exception) {
                    Log.e("storeDataInDb", "Error processing data: ${e.message}")
                    throw e
                }
            }
        }
    }


    private fun setNoaaAlertCollector(context: Context){
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
                Log.e("setNoaaAlertCollector", e.stackTraceToString())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setHpDataCollector(context: Context) {
        if (DEBUG_REPOSITORY) Log.d("setHpDataCollector", "setHpDat Collector")

        scope.launch {
            try {
                val latestData =
                    getLatestHpValuesFromDb(fileLogger, context, db) as Flow<HemisphericPowerData>
                latestData.collect { hpData ->
                    checkIfNotificationIsNecessary(hpData)
                    if (DEBUG_REPOSITORY) Log.d("HpDataCollector", hpData.hpNorth.toString())
                    _latestHpData.postValue(hpData)
                }
            } catch (e: Exception) {
                fileLogger.writeLogsToInternalStorage(
                    context,
                    "SpaceWeatherRepository Init Error: \n " + "unable to instantiate database:\n" + "${e.stackTraceToString()} "
                )
                Log.e("HpDataCollector", e.stackTraceToString())
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun setAceDataCollector(applicationContext: Context) {
        if (DEBUG_REPOSITORY) Log.d("setAceDataCollector", "setAceDataCollector")
        scope.launch {
            try {
                val latestData = getLatestAceValuesFromDb(
                    fileLogger,
                    applicationContext,
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
                fileLogger.writeLogsToInternalStorage(
                    applicationContext, "AceDataCollector\n" + "${e.stackTraceToString()} "
                )
                Log.e("AceDataCollector", e.stackTraceToString())
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
                notificationService.showNoaaAlert(data)
            }
        }


    }


    @Suppress("UNCHECKED_CAST")
    private fun setEpamDataCollector(applicationContext: Context) {
        if (DEBUG_REPOSITORY) Log.d("setEpamDataCollector", "setEpamDataCollector")
        scope.launch {
            try {
                val latestData = getLatestEpamValuesFromDb(
                    fileLogger,
                    applicationContext,
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
                fileLogger.writeLogsToInternalStorage(
                    applicationContext, "AceDataCollector\n" + "${e.stackTraceToString()} "
                )
                Log.e("EpamDataCollector", e.stackTraceToString())
            }
        }
    }
}


