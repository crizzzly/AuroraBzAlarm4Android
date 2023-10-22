package com.crost.aurorabzalarm.repository

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_COL_DT
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.DB_NAME
import com.crost.aurorabzalarm.Constants.HP_COL_DT
import com.crost.aurorabzalarm.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper
import com.crost.aurorabzalarm.repository.util.DataSourceConfig
import com.crost.aurorabzalarm.repository.util.addDataModelInstances
import com.crost.aurorabzalarm.repository.util.downloadDataFromNetwork
import com.crost.aurorabzalarm.repository.util.fetchLatestDataRow
import com.crost.aurorabzalarm.repository.util.getDataSources
import com.crost.aurorabzalarm.repository.util.getLatestAceValuesFromDb
import com.crost.aurorabzalarm.repository.util.getLatestHpValuesFromDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last


class SpaceWeatherRepository(application: Application) {
    private lateinit var db: SpaceWeatherDataBase
    private val downloadManager = DownloadManager()
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var latestValues: MutableMap<String, Any>

    init { // db init
        try {
            Log.i("SpaceWeatherRepository", "init db")
            db =
                Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME).build()
        } catch (e: RuntimeException) {
            Log.e(
                "SpaceWeatherRepository",
                "unable to instantiate database: \n${e.stackTraceToString()}"
            )

            // Handle the error, e.g., show an error message to the user
            // You could use LiveData or callbacks to communicate this error to the UI layer
            // or provide a fallback mechanism to use some default data temporarily
            // Or attempt to recreate the database or perform some recovery actions
            // TODO: skip db actions in fetching process and use returned values instead of db
            Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME).build()
        } catch (e: NullPointerException) {
            Log.e(
                "SpaceWeatherRepository",
                "unable to instantiate database: \n${e.stackTraceToString()}"
            )
            Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME).build()
        }

    }
    

    suspend fun getLatestData(): MutableMap<String, Any> {
        return fetchLatestDataRow(this.db)
    }
    
    
    suspend fun fetchDataAndStore(){
        fetchData()
        delay(100)
        storeDataInDb()
    }

    private fun fetchData() {
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
        Log.d("storeDataInDb", dsConfig.tableName)
            try {
                addDataModelInstances(db, dsConfig.latestData, dsConfig.tableName)

                when (dsConfig.tableName) {
                    ACE_TABLE_NAME ->  setLatestAceVals(dsConfig)
                    HP_TABLE_NAME ->  setLatestHpVals(dsConfig)
                }
            } catch (e: Exception){
                Log.e("fetchDataAndStore", "Error processing data: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun setLatestHpVals(dsConfig: DataSourceConfig){
        val latestVals = getLatestHpValuesFromDb(db).last()
        Log.d(
            "Repo: stored val",
            "${dsConfig.tableName} hpVal: ${latestVals.hpNorth}"
        )
        latestValues[HP_COL_DT] = latestVals.datetime
        latestValues[HP_COL_HPN] = latestVals.hpNorth

    }

    private suspend fun setLatestAceVals(dsConfig: DataSourceConfig){
        val latestVals =
            getLatestAceValuesFromDb(db).last() // as AceMagnetometerDataModel
        Log.d("Repo: stored val:", "${dsConfig.tableName} val ${latestVals.bz}")
        latestValues[ACE_COL_DT] = latestVals.datetime
        latestValues[ACE_COL_BZ] = latestVals.bz
    }

}


