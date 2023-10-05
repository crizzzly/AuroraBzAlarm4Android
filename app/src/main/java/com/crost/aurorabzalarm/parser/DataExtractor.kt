package com.crost.aurorabzalarm.parser

import android.util.Log
import org.jsoup.Jsoup

class DataExtractor(valCount: Int){
    val valuesCount = valCount
    fun getSatelliteData(url: String): List<List<String>> {
        Log.d("getAceSatteliteData", "getting data")
        return try {
            val aceDoc = Jsoup.connect(url).get()
            val html = aceDoc.select("body").toString()
            Log.d("readUrl - ACE", html)
            val dataRows = extractDataRows(html) //.split("\n")
            dataRows
        } catch (e: Exception) {
            Log.e("getSatelliteData", "$url\n ${e.stackTraceToString()}")
            emptyList<List<String>>()
        }
    }

    fun extractDataRows(dataString: String): List<List<String>>{
        var rows = mutableListOf<List<String>>()

        val splitted = dataString.split("\n")[1].split("#")
        Log.d("splitted", splitted.toString())
        var splittedList = splitted[splitted.size-1].split(" ")
        splittedList = splittedList.subList(1, splittedList.size-1)

        val rowCount = splittedList.size % valuesCount

        for (i in 0..rowCount){
            val pos0 = i * valuesCount
            val pos1 = i * valuesCount + valuesCount
            val row = splittedList.subList(pos0, pos1)
            Log.d("Row$i", row.toString())
            rows.add(row)
        }

        return rows
    }


}
