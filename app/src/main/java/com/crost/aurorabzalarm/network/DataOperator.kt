package com.crost.aurorabzalarm.network

import android.content.Context
import com.crost.aurorabzalarm.data.DataSourceConfig
import com.crost.aurorabzalarm.data.NoaaAlertDataHandler
import com.crost.aurorabzalarm.data.getDataSources
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger


class DataOperator(applicationContext: Context) {
    private val fileLogger = FileLogger.getInstance(applicationContext)
    private val downloadManager = DownloadManager(applicationContext)
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val noaaAlertHandler = NoaaAlertDataHandler()
    private val exceptionHandler = ExceptionHandler.getInstance(applicationContext)


    suspend fun fetchData(context: Context): List<List<Any>>{
        val allTables = mutableListOf<List<Any>>()
        for (dsConfig in dataSourceConfigs) {
//            Log.i("fetchData", dsConfig.url)
            try {
                val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)

                val convertedDataTable = handleIncomingData(
                    context, dsConfig, downloadedData
                )
                dsConfig.latestData = convertedDataTable

                allTables.add( convertedDataTable)
            } catch (e: Exception) {
                val msg = "Error processing data: ${e.message}"
                exceptionHandler.handleExceptions(context, "fetchDataAndStore", msg)
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
        return when (dsConfig.tableName) {
            ALERTS_PSEUDO_TABLE_NAME -> {
                val data = parser.parseAlertJson(downloadedData)
                noaaAlertHandler.checkForRelevantAlerts(data)
                data
            }
            EPAM_TABLE_NAME -> {
                EPAM_TABLE_NAME
                val data = parser.parseIMFJson(downloadedData)
                data
            }
            ACE_TABLE_NAME -> {
                parser.parseSolarWindJson(downloadedData)
            }
            else -> {
                emptyList()
            }
        }
    }
}
