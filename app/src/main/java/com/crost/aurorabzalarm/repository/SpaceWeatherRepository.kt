package com.crost.aurorabzalarm.repository

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.DB_NAME
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper
import com.crost.aurorabzalarm.repository.util.DataSourceConfig
import com.crost.aurorabzalarm.repository.util.addDataModelInstances
import com.crost.aurorabzalarm.repository.util.downloadDataFromNetwork
import com.crost.aurorabzalarm.repository.util.fetchLatestData
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
    private lateinit var returnValues: MutableMap<String, Any>

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


    suspend fun fetchDataAndStoreInDatabase(){ //: SpaceWeatherState {
        try {
            // Fetch data and update database
            val data = fetchDataAndStore()
//            SpaceWeatherState.Success(data)
        } catch (e: Exception) {
            Log.e("fetchDataAndStoreInDatabase", e.stackTraceToString())
//            SpaceWeatherState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getLatestData(): MutableMap<String, Any> {
        return fetchLatestData(this.db)
    }

    private suspend fun fetchDataAndStore(): MutableMap<String, Any> {
        for (dsConfig in dataSourceConfigs) {
        Log.i("fetchDataAndStore",
            "dsconfig url: \n${dsConfig.url}")

            try{
                val convertedDataTable = downloadDataFromNetwork(
                    dsConfig, downloadManager, parser, dataShaper
                )

                delay(100)
                addDataModelInstances(db, convertedDataTable, dsConfig.table_name)

                when (dsConfig.table_name) {
                    ACE_TABLE_NAME ->  setLatestAceVals(dsConfig)
                    HP_TABLE_NAME ->  setLatestHpVals(dsConfig)
                }
            } catch (e: Exception){
                Log.e("fetchDataAndStore", "Error processing data: ${e.message}")
                throw e
            }
        }
        return returnValues
    }

    private suspend fun setLatestHpVals(dsConfig: DataSourceConfig){
        val latestVals = getLatestHpValuesFromDb(db).last()
        Log.d(
            "Repo: stored val",
            "${dsConfig.table_name} hpVal: ${latestVals.hpNorth}"
        )
        returnValues["datetime"] = latestVals.datetime
        returnValues["HpVal"] = latestVals.hpNorth

    }

    private suspend fun setLatestAceVals(dsConfig: DataSourceConfig){
        val latestVals =
            getLatestAceValuesFromDb(db).last() // as AceMagnetometerDataModel
        Log.d("Repo: stored val:", "${dsConfig.table_name} val ${latestVals.bz}")
        returnValues["datetime"] = latestVals.datetime
        returnValues["bz"] = latestVals.bz
    }

}


