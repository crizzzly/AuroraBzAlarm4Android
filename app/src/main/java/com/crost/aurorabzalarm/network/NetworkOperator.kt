package com.crost.aurorabzalarm.network

import android.content.Context
import android.util.Log
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.NoaaAlertHandler
import com.crost.aurorabzalarm.network.parser.converter.DataShaper
import com.crost.aurorabzalarm.repository.DataSourceConfig
import com.crost.aurorabzalarm.repository.getDataSources
import com.crost.aurorabzalarm.utils.Constants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.FileLogger


class NetworkOperator(applicationContext: Context) {
    private val fileLogger = FileLogger.getInstance(applicationContext)
    private val downloadManager = DownloadManager(applicationContext)
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val dataShaper = DataShaper()
    private val noaaAlertHandler = NoaaAlertHandler()




    suspend fun fetchData(context: Context): MutableMap<String, MutableList<MutableMap<String, Any>>> {
        val allTables = mutableMapOf<String, MutableList<MutableMap<String, Any>>>()
        for (dsConfig in dataSourceConfigs) {
//            Log.i("fetchData", dsConfig.url)

            try {
                val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)

                val convertedDataTable = handleIncomingData(
                    context, dsConfig, downloadedData
                )
                dsConfig.latestData = convertedDataTable as MutableList<MutableMap<String, Any>>

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




    private suspend fun handleIncomingData(
        context: Context,
        dsConfig: DataSourceConfig,
        downloadedData:String,
    ): List<Any> {
        if (dsConfig.tableName == ALERTS_PSEUDO_TABLE_NAME){
            val data = parser.parseJson(downloadedData)
            noaaAlertHandler.handleAlerts(data)
            return data
//            Log.d("handleIncomingData", downloadedData)
//            return mutableListOf<MutableMap<String, Any>>()
        } else {
            val valuesCount = dsConfig.keys.size
//        val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)
            val parsedDataTable =
                parser.parseData(context, fileLogger, downloadedData, dsConfig.keys, valuesCount)

            return dataShaper.convertData(dsConfig, parsedDataTable)
        }
    }


}
