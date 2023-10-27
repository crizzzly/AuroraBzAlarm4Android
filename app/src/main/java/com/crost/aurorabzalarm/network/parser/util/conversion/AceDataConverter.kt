package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log
import com.crost.aurorabzalarm.Constants.ACE_COL_BT
import com.crost.aurorabzalarm.Constants.ACE_COL_BX
import com.crost.aurorabzalarm.Constants.ACE_COL_BY
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_COL_DT
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
                Log.e("ACEConverter", "date"
                        + e.stackTraceToString())
                Date(convertToLocalEpochMillis(1970, 1, 1, 0))
            }

            try {
                bx = map["Bx"]?.toDouble()!!
                by = map["By"]?.toDouble()!!
                bz = map["Bz"]?.toDouble()!!
                bt = map["Bt"]?.toDouble()!!
            } catch (e: Exception) {
                Log.e("ACEConverter", "Bx, By, Bz"
                        + e.stackTraceToString())
            }

            row[ACE_COL_DT] = datetime
            row[ACE_COL_BX] = bx
            row[ACE_COL_BY] = by
            row[ACE_COL_BZ] = bz
            row[ACE_COL_BT] = bt

            converted.add(row)
        }
        Log.d("ACEConverter", "${converted.last()[ACE_COL_DT]}, ${converted.last()[ACE_COL_BZ]}")
        return converted
    }
}







