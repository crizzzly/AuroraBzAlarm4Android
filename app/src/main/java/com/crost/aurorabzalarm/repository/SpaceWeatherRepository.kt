package com.crost.aurorabzalarm.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import com.crost.aurorabzalarm.network.NetworkOperator
import com.crost.aurorabzalarm.data.getLatestAceValuesFromDb
import com.crost.aurorabzalarm.data.getLatestEpamValuesFromDb
import com.crost.aurorabzalarm.data.getLatestHpValuesFromDb
import com.crost.aurorabzalarm.data.saveDataModelInstances
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.DB_NAME
import com.crost.aurorabzalarm.utils.Constants.EPAM_COL_SPEED
import com.crost.aurorabzalarm.utils.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.utils.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.utils.datetime_utils.formatTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

const val DEBUG_REPOSITORY = false

class SpaceWeatherRepository(application: Application) {
    private val networkOperator = NetworkOperator()
    private lateinit var db: SpaceWeatherDataBase
    private val scope = CoroutineScope(Dispatchers.IO)


    private var _latestHpData = MutableLiveData(
        HemisphericPowerData(0, -999, -999)
    )
    private var _latestAceData = MutableLiveData(
        AceMagnetometerData(0, -999.9, -999.9, -999.9, -999.9)
    )
    private var _latestEpamData = MutableLiveData(
        AceEpamData(0, -9999.9, -9999.9, -9999.9)
    )

    val latestHpData get() = _latestHpData
    val latestAceData get() = _latestAceData
    val latestEpamData get() = _latestEpamData

    // Handle the error, e.g., show an error message to the user
    // You could use LiveData or callbacks to communicate this error to the UI layer
    // or provide a fallback mechanism to use some default data temporarily
    // Or attempt to recreate the database or perform some recovery actions


    init {
        var retryCount = 0

        do {
            try {  // db init
                if (DEBUG_REPOSITORY) Log.i("SpaceWeatherRepository", "init db")
                db =
                    Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME)
//                        .addMigrations(migration1to2, migration1to3, migration2to3, )
//                        .fallbackToDestructiveMigration() // This line ensures any existing database will be cleared
                        .build()
                retryCount = 3
            } catch (e: RuntimeException) {
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount++
//                delay()
            } catch (e: NullPointerException) {
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount++
            }
        } while (retryCount < MAX_RETRY_COUNT)

        setAceDataCollector()
        setHpDataCollector()
        setEpamDataCollector()

        scope.launch {
            fetchDataAndStore()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setHpDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setHpDataCollector", "setHpDat Collector")

        scope.launch {
            try {
                val latestData =  getLatestHpValuesFromDb(db) as Flow<HemisphericPowerData>
                latestData.collect{hpData ->
                    if (DEBUG_REPOSITORY) Log.d("HpDataCollector", hpData.hpNorth.toString())
                    _latestHpData.postValue(hpData)
                }
            } catch (e: Exception) {
                Log.e("HpDataCollector", e.stackTraceToString())
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun setAceDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setAceDataCollector", "setAceDataCollector")
        scope.launch {
            try {
                val latestData = getLatestAceValuesFromDb(db) as Flow<AceMagnetometerData>

                latestData.collect{aceData ->
                    if(DEBUG_REPOSITORY){
                        Log.d(
                            "getLatestAceData",
                            "${formatTimestamp(aceData.datetime)}: Bz ${aceData.bz} nT"
                        )
                    }
                    _latestAceData.postValue(aceData)
                }
            } catch (e: Exception) {
                Log.e("AceDataCollector", e.stackTraceToString())
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun setEpamDataCollector() {
        if (DEBUG_REPOSITORY) Log.d("setEpamDataCollector", "setEpamDataCollector")
        scope.launch {
            try {
                val latestData =  getLatestEpamValuesFromDb(db) as Flow<AceEpamData>
                latestData.collect{epamData ->
                    if (DEBUG_REPOSITORY) {
                        Log.d(
                            "getLatestEpamData",
                            "${formatTimestamp(epamData.datetime)}: Speed ${epamData.speed} km/s"
                        )

                    }
                    _latestEpamData.postValue(epamData)
                }
            } catch (e: Exception) {
                Log.e("EpamDataCollector", e.stackTraceToString())
            }
        }
    }

    suspend fun fetchDataAndStore() {
        var data: MutableMap<String, MutableList<MutableMap<String, Any>>>
        do {
            data = networkOperator.fetchData()
            delay(500)
        } while (data.isEmpty())

        storeDataInDb(data)
    }

    private suspend fun storeDataInDb(allDataLists: MutableMap<String, MutableList<MutableMap<String, Any>>>) {
        for (table in allDataLists.keys) {
            try {
                saveDataModelInstances(db, allDataLists[table]!!, table)
            } catch (e: Exception) {
                Log.e("storeDataInDb", "Error processing data: ${e.message}")
                throw e
            }
            if (DEBUG_REPOSITORY) {
                when (table) {
                    ACE_TABLE_NAME -> {
                        Log.d(
                            "storingDataInDb",
                            "$table val: ${allDataLists[table]!!.last()[ACE_COL_BZ]}"
                        )
                    }

                    EPAM_TABLE_NAME -> {
                        Log.d(
                            "storingDataInDb",
                            "$table val: ${allDataLists[table]!!.last()[EPAM_COL_SPEED]}"
                        )
                    }

                    HP_TABLE_NAME -> {
                        Log.d(
                            "storingDataInDb",
                            "$table val: ${allDataLists[table]!!.last()[HP_COL_HPN]}"
                        )
                    }
                }
            }
        }
    }
}

