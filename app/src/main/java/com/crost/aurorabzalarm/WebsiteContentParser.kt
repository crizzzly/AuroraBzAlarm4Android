package com.crost.aurorabzalarm

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import org.jsoup.Jsoup
import java.util.Calendar

class WebsiteContentParser(context: Context) {
//    val aceUrl = "https://services.swpc.noaa.gov/text/ace-magnetometer.txt"
    val con = context
    val stringSplitterTable = "#------------------------------------------------------------------------------------------------"
     val aceUrl = ContextCompat.getString(con, R.string.aceValsUrl)
    //val hemPwrUrl = ContextCompat.getString(con, R.string.hpValsUrl)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    fun parseAceSatelliteData(): Boolean{
        val data = getAceSatelliteData()
        val table = extractAceData(data)
        return true
    }
    fun getAceSatelliteData(): String{
        // # UT Date   Time  Julian   of the   ----------------  GSM Coordinates ---------------
        //# YR MO DA  HHMM    Day      Day    S     Bx      By      Bz      Bt     Lat.   Long.
        Log.d("getAceSatteliteData", "getting data")
        return try {
            val aceDoc = Jsoup.connect(aceUrl).get()
            val html = aceDoc.select("body").toString()
            Log.d("readUrl - ACE", html)
            html
        } catch (e: Exception){
            Log.e("getAceSatelliteData", e.stackTraceToString())
            ""
        }
    }

    private fun extractAceData(dataString: String): List<String> {
        var table = dataString.split("\n")[1]
        Log.d("extractAceData0", table)
        Log.d("currentYear", currentYear)
        var splittedTable = table.split(currentYear)
        splittedTable = splittedTable.subList(2, splittedTable.size-1)
        Log.d("splittedTable", splittedTable[splittedTable.size-1])
//        table = "$currentYear $table"
//        Log.d("extractAceData2", table)
//        val slicedTable = table.split(currentYear)
//        Log.d("extractAceData", slicedTable.toString())
        return  splittedTable
    }

    fun getHemisphericPowerData(){
        //# Observation        Forecast            North-Hemispheric-Power-Index    South-Hemispheric-Power-Index-GigaWatts
        //# YYYY-MM-DD_HH:MM   YYYY-MM-DD_HH:MM    GigaWatts    			  GigaWatts
    }

}