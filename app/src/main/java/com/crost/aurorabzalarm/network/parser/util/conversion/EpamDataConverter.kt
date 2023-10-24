package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log
import com.crost.aurorabzalarm.Constants.EPAM_COL_DENSITY
import com.crost.aurorabzalarm.Constants.EPAM_COL_DT
import com.crost.aurorabzalarm.Constants.EPAM_COL_SPEED
import com.crost.aurorabzalarm.Constants.EPAM_COL_TEMP
import java.sql.Date

class EpamDataConverter {
    fun convertEpamData(
        dataTable: List<Map<String, String>>
    ): MutableList<MutableMap<String, Any>>
    {
        val convertedDataTable = mutableListOf<MutableMap<String, Any>>()

//        var dateTimeString = ""
        var protonDensity: Double
        var bulkSpeed: Double
        var ionTemperature: Double

        for (dataMap in dataTable){
            val row = mutableMapOf<String, Any>()
            protonDensity = -9999.9
            bulkSpeed = -9999.9
            ionTemperature = -1.00e+05.toDouble()

            val datetime = try {
                convertToLocalEpochMillis(
                    year = dataMap["YR"]?.toInt()!!,
                    month = dataMap["MO"]?.toInt()!!,
                    day = dataMap["DA"]?.toInt()!!,
                    secOfDay = dataMap["SecOfDay"]?.toInt()!!,
                )
            } catch (e: NullPointerException) {
                Log.e("EpamDataConverter", "date"
                        + e.stackTraceToString())
                Date(convertToLocalEpochMillis(1970, 1, 1, 0))
            }

            try {
                protonDensity = dataMap["ProtonDensity"]?.toDouble()!!
                bulkSpeed = dataMap["BulkSpeed"]?.toDouble()!!
                ionTemperature = dataMap["IonTemperature"]?.toDouble()!!
                Log.d("EpamDataConverter",
                    "values: $protonDensity, $bulkSpeed, $ionTemperature")
            } catch (e: Exception){
                Log.e("EpamDataConverter", e.stackTraceToString())
            }

            row[EPAM_COL_DT] = datetime
            row[EPAM_COL_DENSITY] = protonDensity
            row[EPAM_COL_TEMP] = ionTemperature
            row[EPAM_COL_SPEED] = bulkSpeed

            convertedDataTable.add(row)
        }


        return convertedDataTable
    }

    fun scientificNotationToLong(number: String): Long {
        val doubleValue = number.toDouble()
        return doubleValue.toLong()
    }
}