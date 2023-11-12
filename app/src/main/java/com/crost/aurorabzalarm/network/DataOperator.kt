package com.crost.aurorabzalarm.network

import android.content.Context
import android.util.Log
import com.crost.aurorabzalarm.data.DEBUG
import com.crost.aurorabzalarm.data.DataSourceConfig
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.NoaaAlerts
import com.crost.aurorabzalarm.data.getDataSources
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger
import java.time.LocalDateTime


class DataOperator(applicationContext: Context) {
    private val fileLogger = FileLogger.getInstance(applicationContext)
    private val downloadManager = DownloadManager(applicationContext)
    private val parser = DocumentParser()
    private val dataSourceConfigs = getDataSources()
    private val exceptionHandler = ExceptionHandler.getInstance(applicationContext)


    suspend fun fetchData(context: Context): List<List<Any>>{
        val allTables = mutableListOf<List<Any>>()
        for (dsConfig in dataSourceConfigs) {
//            Log.i("fetchData", dsConfig.url)
            try {
                val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)

                val convertedDataTable = handleIncomingData(
                    dsConfig, downloadedData
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

    private fun handleIncomingData(
        dsConfig: DataSourceConfig,
        downloadedData: String,
    ): List<Any> {
        return when (dsConfig.tableName) {
            ALERTS_PSEUDO_TABLE_NAME -> {
                val data = parser.parseAlertJson(downloadedData)
                // reduce alertsList to latest alerts that happened within the last minutes
                getLatestAlerts (data)
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

    private fun getLatestAlerts(alerts: List<NoaaAlert>):List<NoaaAlert> {
        /*
        * searches the alerts list backwards to ensure to get the latest Alerts/Warnings
        * for KpWarning, KpAlert, SolarStormAlert.
        *
        * @param alerts: list with all NoaaAlerts fetched from Noaa Website
        * @return: list, size 3 with the latest kpAlert, kpWarning, solarStormAlert
        * */
        var kpw = NoaaAlert("0", LocalDateTime.now().minusHours(1L), "")
        var kpa = NoaaAlert("0", LocalDateTime.now().minusHours(1L), "")
        var gsa = NoaaAlert("0", LocalDateTime.now().minusHours(1L), "")

        for (alert in alerts.asReversed()){
                when (alert.id) {
                    in NoaaAlerts.KP_WARNING_IDs -> {
                        kpw = alert
                    }
                    in NoaaAlerts.KP_ALERT_IDs -> {
                        kpa = alert
                    }
                    in NoaaAlerts.GEO_STORM_ALERT_IDs -> {
                        gsa = alert
                    }
                }
        }
        val list = listOf(kpa, kpw, gsa)
        val alertString = "${kpa.id}: ${kpa.datetime}\n" +
        "${kpw.id}, ${kpw.datetime}\n" +
                "${gsa.id}, ${gsa.datetime}+"

        if (DEBUG) Log.d("checkForRelevantAlerts", alertString)
        return list
    }
}
