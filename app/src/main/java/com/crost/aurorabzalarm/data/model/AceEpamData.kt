package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.Constants.EPAM_TABLE_NAME


@Entity(tableName = EPAM_TABLE_NAME)
data class AceEpamData(
    @PrimaryKey(autoGenerate = false)
    var datetime: Long, //source format: YYYY-MM-DD_HH:MM
    var density: Int,
    var speed: Int,
    var temp: Int,

)