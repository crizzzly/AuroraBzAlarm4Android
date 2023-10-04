package com.crost.aurorabzalarm

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import org.jsoup.Jsoup
import java.util.Calendar

class AceSiteContentParser(context: Context) {
    //    val aceUrl = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    val con = context
    val aceUrl = ContextCompat.getString(con, R.string.aceValsUrl)
    val hemPwrUrl = ContextCompat.getString(con, R.string.hpValsUrl)

    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    var aceValuesTable = mutableListOf<MutableMap<String, Float>>()

    fun parseAceSatelliteData(): Boolean {
        val data = getSatelliteData(aceUrl)
        return if (data.isNotEmpty()) {
            extractAceData(data)
            true
        } else {
            false
        }

    }

    fun getSatelliteData(url: String): String {
        Log.d("getAceSatteliteData", "getting data")
        return try {
            val aceDoc = Jsoup.connect(url).get()
            val html = aceDoc.select("body").toString()
            Log.d("readUrl - ACE", html)
            html
        } catch (e: Exception) {
            Log.e("getSatelliteData", "$url\n ${e.stackTraceToString()}")
            ""
        }
    }

    private fun extractAceData(dataString: String){
        //TODO: Rename Function!
        var table = dataString.split("\n")[1]
        Log.d("extractAceData0", table)
        Log.d("currentYear", currentYear)
        var splittedTable = table.split(currentYear)
        splittedTable = splittedTable.subList(2, splittedTable.size - 1)
        if (splittedTable.isNotEmpty()){
            for (row in splittedTable){
                saveMappedAceValues(row)
            }
        }
    }

    private fun saveMappedAceValues(stringVals: String) {
        val tableList = stringVals.split(" ")
        val mappedValues = mutableMapOf<String, Float>()
        val valueNames =
            listOf(
                "YR", "MO", "DA", "Time", "JulianDay", "SecOfDay", "S",
                "Bx", "By", "Bz", "Bt", "Lat", "Long"
            )
        var index = 0
        for (value in tableList) {
            if (value.isNotEmpty()) {
//                Log.d("values in row", "${valueNames[index]}: $value")
                mappedValues[valueNames[index]] = value.toFloat()
                index += 1
            }
        }

        // TODO: Save latest Bz value (in mappedValues) to CurrentSpaceWeatherData
        Log.d("saveMappedAceValues", "Bz: ${mappedValues[" Bz "].toString()} nT")
        aceValuesTable.add(mappedValues)
    }
}
