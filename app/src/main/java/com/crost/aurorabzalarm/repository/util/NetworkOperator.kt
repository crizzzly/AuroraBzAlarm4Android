package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper


class NetworkOperator(){
    private val downloadManager = DownloadManager()
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()


    suspend fun fetchData(): MutableMap<String, MutableList<MutableMap<String, Any>>> {
        var allTables = mutableMapOf<String, MutableList<MutableMap<String, Any>>>()
        for (dsConfig in dataSourceConfigs) {
            Log.i("fetchData", dsConfig.url)

            try {
                val convertedDataTable = downloadDataFromNetwork(
                    dsConfig, downloadManager, parser, dataShaper
                )
                dsConfig.latestData = convertedDataTable
                allTables.put(dsConfig.tableName, convertedDataTable)
//                return convertedDataTable
            } catch (e: Exception) {
                Log.e("fetchDataAndStore", "Error processing data: ${e.message}")
                throw e
            }
        }
        return allTables
    }



    private suspend fun downloadDataFromNetwork(
        dsConfig: DataSourceConfig,
        downloadManager:DownloadManager,
        parser: DocumentParser,
        dataShaper:DataShaper
    ): MutableList<MutableMap<String, Any>> {
        val valuesCount = dsConfig.keys.size
        val downloadedDataTable = downloadManager.loadSatelliteDatasheet(dsConfig.url)
        val parsedDataTable = parser.parseData(downloadedDataTable, dsConfig.keys, valuesCount)
        val convertedTable = dataShaper.convertData(dsConfig, parsedDataTable)

        when (dsConfig.tableName) {
            Constants.ACE_TABLE_NAME -> Log.d(
                "downloadDataFromNetwork",
                "${dsConfig.tableName} Bz: ${convertedTable[convertedTable.size - 1][Constants.ACE_COL_BZ]} "
            )

            Constants.HP_TABLE_NAME -> Log.d(
                "downloadDataFromNetwork",
                "${dsConfig.tableName} Hp: ${convertedTable[convertedTable.size - 1][Constants.HP_COL_HPN]} "
            )

            Constants.EPAM_TABLE_NAME -> Log.d(
                "downloadDataFromNetwork",
                "${dsConfig.tableName} Speed: ${convertedTable[convertedTable.size - 1][Constants.EPAM_COL_SPEED]} "
            )
        }
        return convertedTable
    }


}
