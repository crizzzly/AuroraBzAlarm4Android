package com.crost.aurorabzalarm.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.crost.aurorabzalarm.Constants.DB_NAME
import com.crost.aurorabzalarm.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper
import com.crost.aurorabzalarm.repository.util.downloadDataFromNetwork
import com.crost.aurorabzalarm.repository.util.getDataSources
import com.crost.aurorabzalarm.repository.util.getLatestAceValuesFromDb
import com.crost.aurorabzalarm.repository.util.getLatestEpamValuesFromDb
import com.crost.aurorabzalarm.repository.util.getLatestHpValuesFromDb
import com.crost.aurorabzalarm.repository.util.saveDataModelInstances
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class SpaceWeatherRepository(application: Application){

    private lateinit var db: SpaceWeatherDataBase
    private val downloadManager = DownloadManager()
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()
    private val scope = CoroutineScope(Dispatchers.IO)


    private var _latestHpValue = MutableLiveData(
        HemisphericPowerData(0, 0, 0)
    )
    private var _latestAceValue = MutableLiveData(
        AceMagnetometerData(0, -999.9, -999.9, -999.9, -999.9)
    )
    private var _latestEpamValue = MutableLiveData(
            AceEpamData(0, -999.9, -999.9, -999.9)
        )

    val latestHpValue get() = _latestHpValue
    val latestAceValue get() = _latestAceValue

    val latestEpamValue get() = _latestEpamValue

    // Handle the error, e.g., show an error message to the user
    // You could use LiveData or callbacks to communicate this error to the UI layer
    // or provide a fallback mechanism to use some default data temporarily
    // Or attempt to recreate the database or perform some recovery actions


    init {
        var retryCount = 0

        do {
            try {  // db init
                Log.i("SpaceWeatherRepository", "init db")
                db =
                    Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME)
//                        .addMigrations(migration1to2)
//                        .fallbackToDestructiveMigration() // This line ensures any existing database will be cleared
                        .build()
                retryCount = 3
            } catch (e: RuntimeException) {
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount ++
//                delay()
            } catch (e: NullPointerException) {
                Log.e(
                    "SpaceWeatherRepository",
                    "unable to instantiate database: \n${e.stackTraceToString()}"
                )
                retryCount ++
            }
        } while (retryCount < MAX_RETRY_COUNT)
        setAceDataCollector()
        setHpDataCollector()
        setEpamDataCollector()
    }

    private fun setHpDataCollector(){
        scope.launch {
            try {
                getLatestHpData().collect{latestHpData ->
                    Log.d("setHpDataCollector", "Hp: ${latestHpData.hpNorth}")
                    _latestHpValue.postValue(latestHpData)
                }
            } catch (e: Exception){
                Log.e("setHpDataCollector", e.stackTraceToString())
            }
        }

    }
    private fun setAceDataCollector(){
        scope.launch {
            try {
                getLatestAceData().collect{ latestAceData ->
                    Log.d("setAceDataCollector", "Bz: ${latestAceData.bz}")
                    _latestAceValue.postValue(latestAceData)
                }
            } catch (e: Exception){
                Log.e("setAceDataCollector", e.stackTraceToString())
            }
        }
    }


    private fun setEpamDataCollector(){
        scope.launch {
            try {
                getLatestEpamData().collect{ latestEpamData ->
                    Log.d("setEpamDataCollector", "Bz: ${latestEpamData.speed}")
                    _latestEpamValue.postValue(latestEpamData)
                }
            } catch (e: Exception){
                Log.e("setAceDataCollector", e.stackTraceToString())
            }
        }
    }

    suspend fun fetchDataAndStore(){
        fetchData()
        storeDataInDb()
    }

    private suspend fun fetchData() {
        for (dsConfig in dataSourceConfigs) {
            Log.i("fetchData", dsConfig.url)

            try {
                val convertedDataTable = downloadDataFromNetwork(
                    dsConfig, downloadManager, parser, dataShaper
                )
                dsConfig.latestData = convertedDataTable
            } catch (e: Exception) {
                Log.e("fetchDataAndStore", "Error processing data: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun storeDataInDb(){
        for (dsConfig in dataSourceConfigs) {
            Log.d("storingDataInDb", dsConfig.tableName)
            try {
                saveDataModelInstances(db, dsConfig.latestData, dsConfig.tableName)

            } catch (e: Exception){
                Log.e("fetchDataAndStore", "Error processing data: ${e.message}")
                throw e
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun getLatestAceData(): Flow<AceMagnetometerData>{
        return getLatestAceValuesFromDb(db) as Flow<AceMagnetometerData>
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun getLatestHpData(): Flow<HemisphericPowerData>{
        return getLatestHpValuesFromDb(db) as Flow<HemisphericPowerData>
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun getLatestEpamData(): Flow<AceEpamData>{
        return  getLatestEpamValuesFromDb(db) as Flow<AceEpamData>
    }

}


