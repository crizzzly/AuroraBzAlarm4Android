package com.crost.aurorabzalarm.utils.datetime_utils

import java.sql.Date
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


private val localZoneId = ZoneId.systemDefault()


fun dateTimeToLocalMillis(dateTime: LocalDateTime, localZoneId: ZoneId): Long {
    val instant = dateTime.toInstant(ZoneOffset.UTC)
    val zonedDateTime = instant.atZone(localZoneId)
    return zonedDateTime.toInstant().toEpochMilli()
}


fun convertToDate(
    day: Int?,
    month: Int?,
    year: Int?,
    secOfDay: Int?,
): Date {
    val datetimeMillis: Long = convertToLocalEpochMillis(year!!, month!!, day!!, secOfDay!!)
    return Date(datetimeMillis)
}


fun convertToLocalEpochMillis(year:Int, month: Int, day: Int, secOfDay: Int): Long {
    /*
    * converts to local timezone
    * */
    val dateTime = LocalDateTime.of(year, month, day, 0, 0, 0)
        .plusSeconds(secOfDay.toLong())
    return dateTimeToLocalMillis(dateTime, localZoneId)
}


fun convertToLocalDateTime(
    year: Int?,
    month: Int?,
    day: Int?,
    secOfDay: Int?,
): LocalDateTime {
    val gmtZoneId = ZoneId.of("GMT")
    val h = secOfDay?.div(3600)
    val m = (secOfDay?.rem(3600))?.div(60)
    val s = secOfDay?.rem(60)
    val time = LocalDateTime.of(year!!, month!!, day!!, h!!, m!!, s!!)
    //        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    return time.atZone(gmtZoneId).withZoneSameInstant(localZoneId).toLocalDateTime()
}

fun getSecOfDayFromTime(h: Int,m: Int): Int {
    val minInSec = m * 60
    val hourInSec = h * 60 * 60
    return minInSec + hourInSec
}


//fun getCurrentDateTimeAsString(){
//    val now : LocalDateTime = LocalDateTime.now()
//}


fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return formatter.format(localDateTime)
}


fun getTimeOfDataFlight(speed: Float?): Float {

    // TODO: Check this! app sais 33,41 min, spaceWeather says 52 min
    // 1,357e+6 km/h
    val distance = 1500000.0 // km
    val timeInS = distance/speed!!  // km/s
    val timeInM = timeInS/60
//        Log.d("getTimeOfDataFlight", "distance: $distance, speed:$speed, time: $timeInM")
    return timeInM.toFloat()

}

