package com.crost.aurorabzalarm.network.parser.util.conversion

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun dateTimeToLocalMillis(dateTime: LocalDateTime, localZoneId: ZoneId): Long {
    val instant = dateTime.toInstant(ZoneOffset.UTC)
    val zonedDateTime = instant.atZone(localZoneId)
    return zonedDateTime.toInstant().toEpochMilli()
}
