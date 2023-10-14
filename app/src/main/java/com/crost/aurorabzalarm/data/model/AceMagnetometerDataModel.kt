package com.crost.aurorabzalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crost.aurorabzalarm.data.constants.ACE_TABLE_NAME
import java.sql.Date

@Entity(tableName = ACE_TABLE_NAME)
data class AceMagnetometerDataModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
//    val year: Int,
//    val month: Int,
//    val day: Int,
    val observation: Date,
//    val julianDay: Int,
//    val secOfDay: Int,
//    val s: Int,
    val bx: Float,
    val by: Float,
    val bz: Float,
    val bt: Float,
//    val lat: Float,
//    val long: Float,
)

