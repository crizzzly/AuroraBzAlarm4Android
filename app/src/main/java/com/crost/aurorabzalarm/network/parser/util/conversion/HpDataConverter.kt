package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log

class HpDataConverter {
    fun convertHpData(dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        val convertedDataTable = mutableListOf<MutableMap<String, Any>>()

        var hpNorth = 0
        var hpSouth = 0
        var dateString = ""
        var timeString = ""

        for (dataMap in dataTable){
            var row = mutableMapOf<String, Any>()

            try {
                hpNorth = dataMap["HP-North"]?.toInt()!!
                hpSouth = dataMap["HP-South"]?.toInt()!!
                dateString = dataMap["Observation"]!!
            } catch (e: Exception){
                Log.e("DS - convertHpData", e.stackTraceToString())
            }
            val dateTimeList = prepareDateTimeValues(dateString)
            val date = dateTimeList[0]
            val time = dateTimeList[1]
            row["Date"] = date
            row["Time"] = time
            row["HpNorth"] = hpNorth
            row["HpSouth"] = hpSouth
            convertedDataTable.add(row)
//            Log.d("HpDataConverter", "$date, $time, $hpNorth")
        }
        return convertedDataTable

    }

    private fun prepareDateTimeValues(datetime: String): List<Any> {
        val dateTimeList = datetime.split("_")
//        Log.d("HpCOnverter prepare time", "dateTimeList: ${dateTimeList[0]}, ${dateTimeList[1]}")
        val dateString = dateTimeList[0]
        val timeString = dateTimeList[1]
        val dateAsList = dateString.split("-")
        val timeAsList = timeString.split(":")
        val time = convertToLocalDateTime(
            dateAsList[0].toInt(),
            dateAsList[1].toInt(),
            dateAsList[2].toInt(),
            getSecOfDayFromTime(timeAsList[0].toInt(), timeAsList[1].toInt()),
            )
        val date = convertToDate(
            dateAsList[2].toInt(),
            dateAsList[1].toInt(),
            dateAsList[0].toInt(),
            getSecOfDayFromTime(timeAsList[0].toInt(), timeAsList[1].toInt()),
        )
        return listOf(date, time)
    }
}