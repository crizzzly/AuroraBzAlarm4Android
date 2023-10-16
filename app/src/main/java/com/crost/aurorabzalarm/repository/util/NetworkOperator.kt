package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper

fun downloadDataFromNetwork(
    dsConfig: DataSourceConfig,
    downloadManager:DownloadManager,
    parser: DocumentParser,
    dataShaper:DataShaper
    ): MutableList<MutableMap<String, Any>> {
        val valuesCount = dsConfig.keys.size
        val downloadedDataTable = downloadManager.loadSatelliteDatasheet(dsConfig.url)
        val parsedDataTable = parser.parseData(downloadedDataTable, dsConfig.keys, valuesCount)
        val convertedTable = dataShaper.convertData(dsConfig, parsedDataTable)

        when (dsConfig.table_name) {
            Constants.ACE_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.table_name}\n Bz: ${convertedTable[convertedTable.size - 1]["bz"]} "
            )

            Constants.HP_TABLE_NAME -> Log.d(
                "SpaceWeatherRepo - downloaded",
                "${dsConfig.table_name}\n Hp: ${convertedTable[convertedTable.size - 1]["hpNorth"]} "
            )
        }
        return convertedTable
    }
