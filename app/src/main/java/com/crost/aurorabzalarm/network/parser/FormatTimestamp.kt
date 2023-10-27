package com.crost.aurorabzalarm.network.parser

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return formatter.format(localDateTime)
}


