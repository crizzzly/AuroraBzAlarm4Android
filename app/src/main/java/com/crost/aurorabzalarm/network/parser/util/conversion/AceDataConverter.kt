package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log
import java.sql.Date

class AceDataConverter {
    fun convertAceData(dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        val converted = mutableListOf<MutableMap<String, Any>>()

        for (map in dataTable) {
            val row = mutableMapOf<String, Any>()
            var bx = -999.9
            var by = -999.9
            var bz = -999.9
            var bt = -999.9

            val datetime = try {
                convertToLocalEpochMillis(
                    year = map["YR"]?.toInt()!!,
                    month = map["MO"]?.toInt()!!,
                    day = map["DA"]?.toInt()!!,
                    secOfDay = map["SecOfDay"]?.toInt()!!,
                )
            } catch (e: NullPointerException) {
                Log.e("DS - convertAceData", "date"
                        + e.stackTraceToString())
                Date(convertToLocalEpochMillis(1970, 1, 1, 0))
            }


            try {
                bx = map["Bx"]?.toDouble()!!
                by = map["By"]?.toDouble()!!
                bz = map["Bz"]?.toDouble()!!
                bt = map["Bt"]?.toDouble()!!
            } catch (e: Exception) {
                Log.e("DS - convertAceData", "Bx, By, Bz"
                        + e.stackTraceToString())
            }

            row["datetime"] = datetime
            row["bx"] = bx
            row["by"] = by
            row["bz"] = bz
            row["bt"] = bt

            converted.add(row)

//            Log.d("DataShaper ConvACE", "$date, $time, $bx, $by, $bz")
        }
        return converted
    }
}







