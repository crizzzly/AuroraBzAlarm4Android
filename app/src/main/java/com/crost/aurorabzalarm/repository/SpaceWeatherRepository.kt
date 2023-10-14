package com.crost.aurorabzalarm.repository

import android.util.Log
import com.crost.aurorabzalarm.network.download.DownloadManager
import com.crost.aurorabzalarm.network.parser.DocumentParser
import com.crost.aurorabzalarm.network.parser.util.conversion.DataShaper

class SpaceWeatherRepository {
    val downloadManager = DownloadManager()
    val parser = DocumentParser()
    val dataSourceConfigs = getDataSources()
    val dataShaper = DataShaper()


    suspend fun fetchDataAndStoreInDatabase(): MutableList<MutableList<MutableMap<String, Any>>> {
        val convertedDataTables = downloadDataFromNetwork()
        return convertedDataTables
    }

    private suspend fun downloadDataFromNetwork(): MutableList<MutableList<MutableMap<String, Any>>> {
        val convertedDataTables = mutableListOf<MutableList<MutableMap<String, Any>>>()
        for (dsConfig in dataSourceConfigs) {
            val valuesCount = dsConfig.keys.size
            val downloadedData = downloadManager.loadSatelliteDatasheet(dsConfig.url)
            val parsedData = parser.parseData(downloadedData, dsConfig.keys, valuesCount)
            val convertedData = dataShaper.convertData(dsConfig, parsedData)
            convertedDataTables.add(convertedData)
        }
        val latestAceMap = convertedDataTables[0][convertedDataTables[0].size-1]
        val latestHpMap = convertedDataTables[1][convertedDataTables[1].size-1]
        Log.d("SpaceWeatherRepository", "download, latest Bz & Hp Vals\n" +
                "${latestAceMap["Bz"]}, ${latestHpMap["HpNorth"]}")
        return convertedDataTables
    }
}
//
//    private fun mapToDataModels(parsedData: MutableList<Map<String, String>>): List<YourDataModel> {
//        // Implement your logic to map parsed data to your data models
//        // Return a list of YourDataModel objects
//    }
//
//    private suspend fun storeDataInDatabase(dataModels: List<YourDataModel>) {
//        // Store the data models in the database using Room DAOs
//        database.aceDao().insertAll(dataModels)
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


