package com.crost.aurorabzalarm.network.parser.converter

import android.util.Log
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_BT
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_BX
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_BY
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.utils.Constants.ACE_COL_DT
import com.crost.aurorabzalarm.utils.datetime_utils.convertToLocalEpochMillis
import java.sql.Date

class AceDataConverter {
    fun convertAceData(dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        val converted = mutableListOf<MutableMap<String, Any>>()

        for (map in dataTable) {
            val row = mutableMapOf<String, Any>()
            var bx = -999.9f
            var by = -999.9f
            var bz = -999.9f
            var bt = -999.9f

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
                bx = map["Bx"]?.toFloat()!!
                by = map["By"]?.toFloat()!!
                bz = map["Bz"]?.toFloat()!!
                bt = map["Bt"]?.toFloat()!!
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
//        Log.d("ACEConverter", "${converted.last()[ACE_COL_DT]}, ${converted.last()[ACE_COL_BZ]}")
        return converted
    }
}







