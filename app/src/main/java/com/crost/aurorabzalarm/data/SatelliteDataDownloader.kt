package com.crost.aurorabzalarm.data

import android.util.Log
import org.jsoup.Jsoup

class SatelliteDataDownloader(valCount: Int){
    private val valuesCount = valCount
    fun getLatestDataTable(url: String): List<List<String>> {
//        Log.d("getAceSatelliteData", "getting data")
        return try {
            val aceDoc = Jsoup.connect(url).get()
            val html = aceDoc.select("body").toString()
//            Log.d("readUrl - ACE", html)
            val dataRows = extractDataTable(html) //.split("\n")
            dataRows
        } catch (e: Exception) {
            // TODO: make it better somehow
            Log.e("getSatelliteData", "$url\n ${e.stackTraceToString()}")
            emptyList()
        }
    }

    private fun extractDataTable(dataString: String): List<List<String>>{
        val rows = mutableListOf<List<String>>()

        val splitted = dataString.split("\n")[1].split("#")
//        Log.d("splitted", splitted.toString())
        var splittedList = splitted[splitted.size-1].split(" ")
        splittedList = splittedList.subList(1, splittedList.size-1)

        val rowCount = splittedList.size % valuesCount

        for (i in 0..rowCount){
            val pos0 = i * valuesCount
            val pos1 = i * valuesCount + valuesCount
            val row = try {
                splittedList.subList(pos0, pos1)
            } catch (e: IndexOutOfBoundsException){
                splittedList.subList(pos0, pos1-1)
            }
//            Log.d("Row$i", row.toString())
            rows.add(row)
        }
        return rows
    }
}
