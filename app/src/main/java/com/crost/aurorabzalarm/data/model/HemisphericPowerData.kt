package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME

@Entity(tableName = HP_TABLE_NAME)
data class HemisphericPowerData(
    @PrimaryKey(autoGenerate = false)
    var datetime: Long, //source format: YYYY-MM-DD_HH:MM
    var hpNorth: Int,
    var hpSouth: Int,
)