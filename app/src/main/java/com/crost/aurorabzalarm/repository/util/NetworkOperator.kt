package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.EPAM_COL_SPEED
import com.crost.aurorabzalarm.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper

suspend fun downloadDataFromNetwork(
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
            ACE_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.tableName}\n Bz: ${convertedTable[convertedTable.size - 1][ACE_COL_BZ]} "
            )

            HP_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.tableName}\n Hp: ${convertedTable[convertedTable.size - 1][HP_COL_HPN]} "
            )

            EPAM_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.tableName}\n Hp: ${convertedTable[convertedTable.size - 1][EPAM_COL_SPEED]} "
            )
        }
        return convertedTable
    }

