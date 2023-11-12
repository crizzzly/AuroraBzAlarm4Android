package com.crost.aurorabzalarm.utils.datetime_utils

import android.util.Log
import java.sql.Date
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val localZoneId = ZoneId.systemDefault()
private val DEBBUG_TOF = false

fun calculateTimeDifferenceFromNow(endDateTime: LocalDateTime): Duration {
    val startDateTime = LocalDateTime.now()
    //    Log.d("calculateTimeDifferenceFromNow", "now: $startDateTime\n" +
//            "then: $endDateTime\n" +
//            "duration: ${duration.toMinutes()}")
    return Duration.between(startDateTime, endDateTime).abs()
}

fun parseDateTimeString(dateTimeString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    return LocalDateTime.parse(dateTimeString, formatter)
}

fun convertUtcToLocal(utcDateTime: LocalDateTime): LocalDateTime {
    val utcZoneId = ZoneId.of("UTC")
    val targetZoneId = localZoneId

    val utcZonedDateTime = ZonedDateTime.of(utcDateTime, utcZoneId)
    val targetZonedDateTime = utcZonedDateTime.withZoneSameInstant(targetZoneId)

    return targetZonedDateTime.toLocalDateTime()
}


fun dateTimeToLocalMillis(dateTime: LocalDateTime): Long {
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
    return dateTimeToLocalMillis(dateTime)
}






fun formatTimestamp(time: LocalDateTime): String {
//    val instant = Instant.ofEpochMilli(timestamp)
//    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return formatter.format(time)
}


fun getTimeOfDataFlight(speed: Double?): Float {

    // TODO: Check this! app sais 33,41 min, spaceWeather says 52 min
    // 1,357e+6 km/h
    val distance = 1500000.0 // km
    val timeInS = distance/speed!!  // km/s
    val timeInM = timeInS/60
    if(DEBBUG_TOF) Log.d("getTimeOfDataFlight", "distance: $distance, speed:$speed, time: $timeInM")

    return timeInM.toFloat()
}

