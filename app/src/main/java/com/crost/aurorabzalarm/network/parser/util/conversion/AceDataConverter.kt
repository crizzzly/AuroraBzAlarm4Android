package com.crost.aurorabzalarm.network.parser.util.conversion

import android.util.Log
import java.sql.Date
import java.time.LocalDateTime
import java.time.ZoneId

class AceDataConverter()  {
//    lateinit var dsConfig: DataSourceConfig
//    lateinit var dataTable: List<Map<String, String>>
//
    private val localZoneId = ZoneId.systemDefault()
//    var bx = -999.9
//    var by = -999.9
//    var bz = -999.9

    fun convertAceData(dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        val converted = mutableListOf<MutableMap<String, Any>>()

        for (map in dataTable){
            var row = mutableMapOf<String, Any>()
            var bx = -999.9
            var by = -999.9
            var bz = -999.9

            val date = try {
                convertAceDate(
                    year = map["Y"]?.toInt(),
                    month = map["M"]?.toInt(),
                    day = map["D"]?.toInt(),
                    secOfDay = map["SecOfDay"]?.toInt(),
                )
            } catch (e: Exception){
                Log.e("DS - convertAceData", e.stackTraceToString())
                "Error"
            }

            try {
                bx = map["Bx"]?.toDouble()!!
                by = map["By"]?.toDouble()!!
                bz = map["Bz"]?.toDouble()!!
            } catch (e: Exception){
                Log.e("DS - convertAceData", e.stackTraceToString())
            }

            row["date"] = date
            row["Bx"] = bx
            row["By"] = by
            row["Bz"] = bz

            converted.add(row)

            Log.d("DataShaper ConvACE", "$date, $bx, $by, $bz")
        }
        return converted
    }


    private fun convertToLocalEpochMillis(year:Int, month: Int, day: Int, secOfDay: Int): Long {
        /*
        * converts to local timezone
        * */
        val dateTime = LocalDateTime.of(year, month, day, 0, 0, 0)
            .plusSeconds(secOfDay.toLong())
        return dateTimeToLocalMillis(dateTime, localZoneId)
    }



    private fun convertAceDate(
        day: Int?,
        month: Int?,
        year: Int?,
        secOfDay: Int?,
    ): Date {
        val datetimeMillis: Long = convertToLocalEpochMillis(year!!, month!!, day!!, secOfDay!!)
        return Date(datetimeMillis)
    }

    private fun convertAceTime(
        day: Int?,
        month: Int?,
        year: Int?,
        secOfDay: Int?,
    ): LocalDateTime {
        val gmtZoneId = ZoneId.of("GMT")
        val datetimeMillis: Long = convertToLocalEpochMillis(year!!, month!!, day!!, secOfDay!!)
        val h = secOfDay / 3600
        val m = (secOfDay % 3600) / 60
        val s = secOfDay % 60
        val time = LocalDateTime.of(year, month, day, h, m, s )
        val localDateTime = time.atZone(gmtZoneId).withZoneSameInstant(localZoneId).toLocalDateTime()
//        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return localDateTime
    }
}