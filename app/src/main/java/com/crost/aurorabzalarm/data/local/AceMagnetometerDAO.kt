package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.data.constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.model.AceMagnetometerDataModel


@Dao
interface AceMagnetometerDAO {
    @Query("SELECT * FROM $ACE_TABLE_NAME")
    fun getAllData(): List<AceMagnetometerDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: AceMagnetometerDataModel)
}
