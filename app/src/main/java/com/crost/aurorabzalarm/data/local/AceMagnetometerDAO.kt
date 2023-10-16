package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.data.ParserConstants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.model.AceMagnetometerDataModel
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.Continuation


@Dao
interface AceMagnetometerDAO {
    @Query("SELECT * FROM $ACE_TABLE_NAME")
    fun getAllData(): List<AceMagnetometerDataModel>


    @Query("SELECT * FROM $ACE_TABLE_NAME ORDER BY datetime DESC LIMIT 1")
    fun getLastRow(): Flow<AceMagnetometerDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: AceMagnetometerDataModel, continuation: Continuation<AceMagnetometerDataModel>): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<AceMagnetometerDataModel>, continuation: Continuation<AceMagnetometerDataModel>): List<Long>
//    {
//        for (row in data){
//            insertDataRow(row, continuation)
//        }
//    }
}
