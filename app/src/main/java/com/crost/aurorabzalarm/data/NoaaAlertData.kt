package com.crost.aurorabzalarm.data

import java.time.LocalDateTime

const val DEBUG = true
data class NoaaAlert(
    var id: String,
    var datetime: LocalDateTime,
    var message: String,
)
