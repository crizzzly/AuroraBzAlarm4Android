package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.data.model.AceEpamData
import kotlinx.coroutines.flow.Flow

@Dao
interface AceEpamDAO {
    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME}")
    fun getAllData(): Flow<List<AceEpamData>>


    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME} ORDER BY datetime DESC LIMIT 1")
    fun getLastRow(): Flow<AceEpamData>

    @Query("SELECT * FROM ${Constants.EPAM_TABLE_NAME} ORDER BY datetime LIMIT 1")
    fun getFirstRow(): Flow<AceEpamData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: AceEpamData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<AceEpamData>): List<Long>
}