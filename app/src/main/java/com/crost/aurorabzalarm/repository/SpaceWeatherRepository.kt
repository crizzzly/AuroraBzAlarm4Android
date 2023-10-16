package com.crost.aurorabzalarm.repository

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.crost.aurorabzalarm.data.ParserConstants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.ParserConstants.DB_NAME
import com.crost.aurorabzalarm.data.ParserConstants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceMagnetometerDataModel
import com.crost.aurorabzalarm.data.model.HemisphericPowerDataModel
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SpaceWeatherRepository(application: Application) {
    private lateinit var db: SpaceWeatherDataBase
    private val downloadManager = DownloadManager()
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()
    private val scope = CoroutineScope(Dispatchers.IO)
    init {
        try {
            Log.i("SpaceWeatherRepository", "init db")
            db =  Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME).build()
        }catch (e: RuntimeException){
            Log.e("SpaceWeatherRepository", "unable to instantiate database: \n${e.stackTraceToString()}")

            // Handle the error, e.g., show an error message to the user
            // You could use LiveData or callbacks to communicate this error to the UI layer
            // or provide a fallback mechanism to use some default data temporarily
            // Or attempt to recreate the database or perform some recovery actions
            // TODO: skip db actions in fetching process and use returned values instead of db
            Room.databaseBuilder(application, SpaceWeatherDataBase::class.java, DB_NAME).build()
        }

    }
//    private lateinit var db: SpaceWeatherDataBase
//    private var db: SpaceWeatherDataBase = Room
//        .databaseBuilder(con, SpaceWeatherDataBase::class.java, "database-name")
//        .build();

    fun isDatabaseInitialized(): Boolean {
        return db.isOpen
    }

    fun fetchDataAndStoreInDatabase() {

//        scope.launch {
            for (dsConfig in dataSourceConfigs){
                val convertedDataTable = downloadDataFromNetwork(dsConfig)
                addDataModelInstances(convertedDataTable, dsConfig.table_name)
//            var latestVals: Any
                when (dsConfig.table_name){
                    ACE_TABLE_NAME -> {
                        val latestVals =
                            getLatestValuesFromDb(dsConfig.table_name) as AceMagnetometerDataModel
                        Log.d("${dsConfig.table_name} val", latestVals.bz.toString())
                    }
                    HP_TABLE_NAME -> {
                        val latestVals =
                            getLatestValuesFromDb(dsConfig.table_name) as HemisphericPowerDataModel
                        Log.d("${dsConfig.table_name} val", latestVals.hpNorth.toString())
                    }
                }

//            }
        }
    }

    private fun downloadDataFromNetwork(dsConfig: DataSourceConfig): MutableList<MutableMap<String, Any>> {
        val valuesCount = dsConfig.keys.size
        val downloadedDataTable = downloadManager.loadSatelliteDatasheet(dsConfig.url)
        val parsedDataTable = parser.parseData(downloadedDataTable, dsConfig.keys, valuesCount)
        val convertedTable = dataShaper.convertData(dsConfig, parsedDataTable)
        scope.launch {
            when (dsConfig.table_name){
                ACE_TABLE_NAME ->  Log.d("SpaceWeatherRepo - downloaded",
                    "${dsConfig.table_name}\n Bz: ${convertedTable[convertedTable.size-1]["bz"]} ")
                HP_TABLE_NAME ->  Log.d("SpaceWeatherRepo - downloaded",
                    "${dsConfig.table_name}\n Hp: ${convertedTable[convertedTable.size-1]["bz"]} ")
            }
        }
        return convertedTable
    }


    private fun createHpModelInstance(dataTable: MutableList<MutableMap<String, Any>>){
        val instances = mutableListOf<HemisphericPowerDataModel>()
        val existingData = db.hpDao().getAllData() // Assuming you have a DAO method to fetch all data

        for (row in dataTable) {
            // Check if the row already exists in the db based on date and time
            val datetime = row["datetime"] as Long
            if (!existingData.any { it.datetime == datetime }) {
                // If the row doesn't exist, create a new data model instance and add it to the list
                val hpDataModel = HemisphericPowerDataModel(
                    datetime = datetime,
                    hpNorth = row["hpNorth"] as Int,
                    hpSouth = row["hpSouth"] as Int,
                )
                instances.add(hpDataModel)

            }
        }
        scope.launch {
            try {
                // Add new rows to the db

                db.hpDao().insertAll(instances, object : Continuation<HemisphericPowerDataModel> {
                    override val context: CoroutineContext = EmptyCoroutineContext // You can customize the coroutine context if needed

                    override fun resumeWith(result: Result<HemisphericPowerDataModel>) {
                        // Handle the result if necessary
                    }
                })
            } catch (e: Exception){
                Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
                delay(200)
                db.hpDao().insertAll(instances, object : Continuation<HemisphericPowerDataModel> {
                    override val context: CoroutineContext = EmptyCoroutineContext // You can customize the coroutine context if needed

                    override fun resumeWith(result: Result<HemisphericPowerDataModel>) {
                        // Handle the result if necessary
                    }
                })
            }
        }
    }


    private fun createAceModelInstance(dataTable: MutableList<MutableMap<String, Any>>){
        val instances = mutableListOf<AceMagnetometerDataModel>()
        val existingData = db.aceDao().getAllData() // Assuming you have a DAO method to fetch all data

        for (row in dataTable) {
            // Check if the row already exists in the db based on date and time
            val datetime = row["datetime"] as Long
            if (!existingData.any { it.datetime == datetime }) {
                // If the row doesn't exist, create a new data model instance and add it to the list
                val aceDataModel = AceMagnetometerDataModel(
                    datetime = datetime,
                    bx = row["bx"] as Float,
                    by = row["by"] as Float,
                    bz = row["bz"] as Float,
                    bt = row["bt"] as Float
                )
                instances.add(aceDataModel)
            }
        }
        scope.launch {
            try {
                // Add new rows to the db
                withContext(Dispatchers.IO){
                    db.aceDao().insertAll(instances, object : Continuation<AceMagnetometerDataModel> {
                        override val context: CoroutineContext = EmptyCoroutineContext // You can customize the coroutine context if needed

                        override fun resumeWith(result: Result<AceMagnetometerDataModel>) {
                            // Handle the result if necessary
                        }
                    })
                }
            } catch (e: Exception){
                Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
                delay(200)
                withContext(Dispatchers.IO){
                    db.aceDao().insertAll(instances, object : Continuation<AceMagnetometerDataModel> {
                        override val context: CoroutineContext = EmptyCoroutineContext // You can customize the coroutine context if needed

                        override fun resumeWith(result: Result<AceMagnetometerDataModel>) {
                            // Handle the result if necessary
                        }
                    })
                }
            }
        }
    }


    private fun addDataModelInstances(dataTable: MutableList<MutableMap<String, Any>>, tableName: String) {
        when(tableName){
            ACE_TABLE_NAME -> {
                createAceModelInstance(dataTable)
            }
            HP_TABLE_NAME -> {
                createHpModelInstance(dataTable)
            }
        }
    }

    private fun getLatestValuesFromDb(tableName: String): Any? {
        return when(tableName){
            ACE_TABLE_NAME -> {
                scope.launch {
                    try {
                        val latestAceVals = withContext(Dispatchers.IO){
                            db.aceDao().getLastRow()
                        }
                    }catch (e: Exception){
                        Log.e("Repo getLatestValuesFromDb", e.stackTraceToString())
                        delay(200)
                        db.aceDao().getLastRow()
                    }
                }
            }
            HP_TABLE_NAME -> {
                scope.launch{
                    try {
                        val latestHpVals = withContext(Dispatchers.IO) {
                            db.hpDao().getLastRow()
                        }
                    } catch (e: Exception) {
                        Log.e("Repo getLatestValuesFromDb", e.stackTraceToString())
                        delay(200)
                        db.hpDao().getLastRow()
                    }
                }

            }
            else -> {}
        }
    }
}
//
//
//    private suspend fun storeDataInDatabase(dataModels: List<YourDataModel>) {
//        // Store the data models in the db using Room DAOs
//        db.aceDao().insertAll(dataModels)
//    }



//    suspend fun fetchData(): MutableList<MutableList<MutableMap<String, Any>>> {
//        val convertedDataTables = downloadDataFromNetwork(dataSourceConfigs, downloadManager, parser, dataShaper)
//        val convertedDataTables = mutableListOf<MutableList<MutableMap<String, Any>>>()
//        for (dsConfig in dataSourceConfigs) {
//            val valuesCount = dsConfig.keys.size
//            val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)
//            val parsedData = parser.parseData(downloadedData, dsConfig.keys, valuesCount)
//            val convertedData = dataShaper.convertData(dsConfig, parsedData)
//            convertedDataTables.add(convertedData)
//        }
//        return convertedDataTables
//    }
//}


