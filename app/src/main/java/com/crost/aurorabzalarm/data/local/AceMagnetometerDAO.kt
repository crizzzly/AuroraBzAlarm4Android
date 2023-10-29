package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import kotlinx.coroutines.flow.Flow


@Dao
interface AceMagnetometerDAO {
    @Query("SELECT * FROM $ACE_TABLE_NAME")
    fun getAllData(): Flow<List<AceMagnetometerData>>


    @Query("SELECT * FROM $ACE_TABLE_NAME ORDER BY datetime DESC LIMIT 1")
    fun getLastRow(): Flow<AceMagnetometerData>

    @Query("SELECT * FROM $ACE_TABLE_NAME ORDER BY datetime LIMIT 1")
    fun getFirstRow(): Flow<AceMagnetometerData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: AceMagnetometerData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<AceMagnetometerData>): List<Long>
}
