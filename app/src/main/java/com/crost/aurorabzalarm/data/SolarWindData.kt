package com.crost.aurorabzalarm.data

import java.time.LocalDateTime

data class SolarWindData(
    var dateTime: LocalDateTime,
    var density: Double,
    var speed: Double,
    var temperature: Double
)