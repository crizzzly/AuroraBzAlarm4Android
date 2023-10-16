package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log

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
                Log.e("DS - convertHpData", e.stackTraceToString())
            }
            val datetime = prepareDateTimeValues(datetimeString)
            row["datetime"] = datetime
            row["hpNorth"] = hpNorth
            row["hpSouth"] = hpSouth
            convertedDataTable.add(row)
//            Log.d("HpDataConverter", "$date, $time, $hpNorth")
        }
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