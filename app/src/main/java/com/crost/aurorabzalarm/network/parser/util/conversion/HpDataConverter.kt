package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log
import com.crost.aurorabzalarm.Constants.HP_COL_DT
import com.crost.aurorabzalarm.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.Constants.HP_COL_HPS

class HpDataConverter {
    fun convertHpData(dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        val convertedDataTable = mutableListOf<MutableMap<String, Any>>()

        var hpNorth = 0
        var hpSouth = 0
        var datetimeString = ""

        for (dataMap in dataTable){
            var row = mutableMapOf<String, Any>()
            hpNorth = 0
            hpSouth = 0

            try {
                hpNorth = dataMap["HPNorth"]?.toInt()!!
                hpSouth = dataMap["HPSouth"]?.toInt()!!
                datetimeString = dataMap["Observation"]!!
            } catch (e: Exception){
                Log.e("HpDataConverter", e.stackTraceToString())
            }
            val datetime = prepareDateTimeValues(datetimeString)
            row[HP_COL_DT] = datetime
            row[HP_COL_HPN] = hpNorth
            row[HP_COL_HPS] = hpSouth
            convertedDataTable.add(row)
//            Log.d("HpDataConverter", "$datetime, $hpNorth")
        }
        Log.d(
            "HpDataConverter",
            "converted ${convertedDataTable.size} rows. last: ${convertedDataTable.last()[HP_COL_DT]}, ${convertedDataTable.last()[HP_COL_HPN]}"
        )
        return convertedDataTable

    }

    private fun prepareDateTimeValues(datetime: String): Long {
        val dateTimeList = datetime.split("_")
//        Log.d("HpCOnverter prepare time", "dateTimeList: ${dateTimeList[0]}, ${dateTimeList[1]}")
        val datetimeString = dateTimeList[0]
        val timeString = dateTimeList[1]
        val dateAsList = datetimeString.split("-")
        val timeAsList = timeString.split(":")

        return convertToLocalEpochMillis(
            dateAsList[0].toInt(),
            dateAsList[1].toInt(),
            dateAsList[2].toInt(),
            getSecOfDayFromTime(timeAsList[0].toInt(), timeAsList[1].toInt()),
        )
    }
}