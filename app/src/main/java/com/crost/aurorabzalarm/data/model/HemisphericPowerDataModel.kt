package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.data.constants.HP_TABLE_NAME
import java.util.Date

@Entity(tableName = HP_TABLE_NAME)
data class HemisphericPowerDataModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val observation: Date, //source format: YYYY-MM-DD_HH:MM
    val forecast: Date,
    val northernHemisphericPower: Int,
    val southernHemisphericPower: Int,
)