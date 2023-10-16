package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME

@Entity(tableName = ACE_TABLE_NAME)
data class AceMagnetometerDataModel(
    @PrimaryKey(autoGenerate = false)
    var datetime: Long,
    var bx: Double,
    var by: Double,
    var bz: Double,
    var bt: Double,
)

