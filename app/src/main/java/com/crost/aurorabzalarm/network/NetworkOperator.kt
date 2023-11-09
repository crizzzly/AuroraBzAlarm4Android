package com.crost.aurorabzalarm.network

import android.content.Context
import android.util.Log
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.converter.DataShaper
import com.crost.aurorabzalarm.repository.DataSourceConfig
import com.crost.aurorabzalarm.repository.getDataSources
import com.crost.aurorabzalarm.utils.FileLogger


class NetworkOperator(applicationContext: Context) {
    private val fileLogger = FileLogger.getInstance(applicationContext)
    private val downloadManager = DownloadManager(applicationContext)
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()




    suspend fun fetchData(context: Context): MutableMap<String, MutableList<MutableMap<String, Any>>> {
        val allTables = mutableMapOf<String, MutableList<MutableMap<String, Any>>>()
        for (dsConfig in dataSourceConfigs) {
//            Log.i("fetchData", dsConfig.url)

            try {
                val convertedDataTable = downloadDataFromNetwork(
                    context, dsConfig, downloadManager, parser, dataShaper
                )
                dsConfig.latestData = convertedDataTable
                allTables[dsConfig.tableName] = convertedDataTable
//                return convertedDataTable
            } catch (e: Exception) {
                val msg = "Error processing data: ${e.message}"
                fileLogger.writeLogsToInternalStorage(
                    context,
                    "storingDataInDb\n" +
                            msg)
                Log.e("fetchDataAndStore", msg)
                throw e
            }
        }
        return allTables
    }



    private suspend fun downloadDataFromNetwork(
        context: Context,
        dsConfig: DataSourceConfig,
        downloadManager:DownloadManager,
        parser: DocumentParser,
        dataShaper: DataShaper
    ): MutableList<MutableMap<String, Any>> {
        val valuesCount = dsConfig.keys.size
        val downloadedDataTable = downloadManager.loadSatelliteDatasheet(dsConfig.url)
        val parsedDataTable = parser.parseData(context, fileLogger, downloadedDataTable, dsConfig.keys, valuesCount)
        val convertedTable = dataShaper.convertData(dsConfig, parsedDataTable)

//        when (dsConfig.tableName) {
//            Constants.ACE_TABLE_NAME -> Log.d(
//                "downloadDataFromNetwork",
//                "${dsConfig.tableName} Bz: ${convertedTable[convertedTable.size - 1][Constants.ACE_COL_BZ]} "
//            )
//
//            Constants.HP_TABLE_NAME -> Log.d(
//                "downloadDataFromNetwork",
//                "${dsConfig.tableName} Hp: ${convertedTable[convertedTable.size - 1][Constants.HP_COL_HPN]} "
//            )
//
//            Constants.EPAM_TABLE_NAME -> Log.d(
//                "downloadDataFromNetwork",
//                "${dsConfig.tableName} Speed: ${convertedTable[convertedTable.size - 1][Constants.EPAM_COL_SPEED]} "
//            )
//        }
        return convertedTable
    }


}
