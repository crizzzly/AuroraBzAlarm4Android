package com.crost.aurorabzalarm.data

import java.time.LocalDateTime

data class ImfData(
    var dateTime: LocalDateTime,
    var bx: Double,
    var by: Double,
    var bz: Double,
    var bt: Double
)
class ImfDataHandler {
}