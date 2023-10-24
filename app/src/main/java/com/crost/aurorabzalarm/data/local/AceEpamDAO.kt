package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import kotlinx.coroutines.flow.Flow

@Dao
interface AceEpamDAO {
    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME}")
    fun getAllData(): Flow<List<AceMagnetometerData>>


    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME} ORDER BY datetime DESC LIMIT 1")
    fun getLastRow(): Flow<AceMagnetometerData>

    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME} ORDER BY datetime LIMIT 1")
    fun getFirstRow(): Flow<AceMagnetometerData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: AceMagnetometerData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<AceMagnetometerData>): List<Long>
}