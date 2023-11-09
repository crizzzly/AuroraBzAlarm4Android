package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME

@Entity(tableName = ACE_TABLE_NAME)
data class AceMagnetometerData(
    @PrimaryKey(autoGenerate = false)
    var datetime: Long,
    var bx: Float,
    var by: Float,
    var bz: Float,
    var bt: Float,
)

