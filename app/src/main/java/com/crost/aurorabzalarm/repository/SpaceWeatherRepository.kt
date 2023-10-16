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
        }

    }
//    private lateinit var db: SpaceWeatherDataBase
//    private var db: SpaceWeatherDataBase = Room
//        .databaseBuilder(con, SpaceWeatherDataBase::class.java, "database-name")
//        .build();

    suspend fun fetchDataAndStoreInDatabase() {

//        scope.launch {
        for (dsConfig in dataSourceConfigs) {
            val convertedDataTable = downloadDataFromNetwork(dsConfig)
            addDataModelInstances(convertedDataTable, dsConfig.table_name)
            when (dsConfig.table_name) {
                ACE_TABLE_NAME -> {
                    val latestVals =
                        getLatestAceValuesFromDb() // as AceMagnetometerDataModel
                    Log.d("Repo: stored val:", "${dsConfig.table_name} val ${latestVals.bz}")
                }

                HP_TABLE_NAME -> {
                    val latestVals =
                        getLatestHpValuesFromDb() as HemisphericPowerDataModel
                    Log.d("Repo: stored val", "${dsConfig.table_name} hpVal: ${latestVals.hpNorth.toString()}")
                }
            }
        }
    }

    private fun downloadDataFromNetwork(dsConfig: DataSourceConfig): MutableList<MutableMap<String, Any>> {
        val valuesCount = dsConfig.keys.size
        val downloadedDataTable = downloadManager.loadSatelliteDatasheet(dsConfig.url)
        val parsedDataTable = parser.parseData(downloadedDataTable, dsConfig.keys, valuesCount)
        val convertedTable = dataShaper.convertData(dsConfig, parsedDataTable)

        when (dsConfig.table_name) {
            ACE_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.table_name}\n Bz: ${convertedTable[convertedTable.size - 1]["bz"]} "
            )

            HP_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.table_name}\n Hp: ${convertedTable[convertedTable.size - 1]["hpNorth"]} "
            )
        }

        return convertedTable
    }


    private suspend fun createHpModelInstance(dataTable: MutableList<MutableMap<String, Any>>) {
        val instances = mutableListOf<HemisphericPowerDataModel>()
        val existingData =
            db.hpDao().getAllData() // Assuming you have a DAO method to fetch all data

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
//        scope.launch {
            try {
                // Add new rows to the db
                db.hpDao().insertAll(instances)
            } catch (e: Exception) {
                Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
                delay(200)
                db.hpDao().insertAll(instances)
            }
        }
//    }


    private suspend fun createAceModelInstance(dataTable: MutableList<MutableMap<String, Any>>) {
        val instances = mutableListOf<AceMagnetometerDataModel>()
        val existingData =
            db.aceDao().getAllData() // Assuming you have a DAO method to fetch all data

        for (row in dataTable) {
            // Check if the row already exists in the db based on date and time
            val datetime = row["datetime"] as Long
            if (!existingData.any { it.datetime == datetime }) {
                // If the row doesn't exist, create a new data model instance and add it to the list
                val aceDataModel = AceMagnetometerDataModel(
                    datetime = datetime,
                    bx = row["bx"] as Double,
                    by = row["by"] as Double,
                    bz = row["bz"] as Double,
                    bt = row["bt"] as Double
                )
                instances.add(aceDataModel)
            }
        }
            try {
                // Add new rows to the db
                    db.aceDao().insertAll(instances)

            } catch (e: Exception) {
                Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
                db.aceDao().insertAll(instances)
            }
    }


    private suspend fun addDataModelInstances(
        dataTable: MutableList<MutableMap<String, Any>>,
        tableName: String,
    ) {
        when (tableName) {
            ACE_TABLE_NAME -> {
                createAceModelInstance(dataTable)
            }

            HP_TABLE_NAME -> {
                createHpModelInstance(dataTable)
            }
        }
    }

    private fun getLatestAceValuesFromDb(): AceMagnetometerDataModel {
        return try {
            db.aceDao().getLastRow() as AceMagnetometerDataModel
        } catch (e: Exception) {
            Log.e("Repo getLatestValuesFromDb", e.stackTraceToString())
            //                delay(200)
            db.aceDao().getLastRow() as AceMagnetometerDataModel
        }
    }


    private fun getLatestHpValuesFromDb(): HemisphericPowerDataModel? {
        return try {
            db.hpDao().getLastRow()
        } catch (e: Exception) {
            Log.e("Repo getLatestHpValuesFromDb", e.stackTraceToString())
//                        delay(200)
            db.hpDao().getLastRow()
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


