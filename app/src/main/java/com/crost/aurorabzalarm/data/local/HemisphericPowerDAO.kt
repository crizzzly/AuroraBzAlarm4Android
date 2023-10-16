package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.data.ParserConstants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.model.HemisphericPowerDataModel

//insertAll Method: In your AceMagnetometerDAO interface, you have defined the insertAll method,
// but you left its implementation empty. If you intend to use this method, you should provide the
// implementation logic to insert multiple instances into the database.
//
//Conflict Strategy: You're using OnConflictStrategy.REPLACE for your insert operations, which
// means if there is a conflict (i.e., if a row with the same primary key already exists),
// it will be replaced. Ensure this behavior aligns with your requirements.
//
//Query Methods: You have defined query methods to fetch all data and retrieve the
// last row from the database. Make sure these queries return the data you expect and handle
// edge cases appropriately.

@Dao
interface HemisphericPowerDAO {
    @Query("SELECT * FROM $HP_TABLE_NAME")
    fun getAllData(): List<HemisphericPowerDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: HemisphericPowerDataModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<HemisphericPowerDataModel>)
//    {
//        for (row in data){
//            insertDataRow(row, continuation)
//        }
//    }

    @Query("SELECT * FROM $HP_TABLE_NAME ORDER BY datetime DESC LIMIT 1")
    fun getLastRow(): HemisphericPowerDataModel
}

//    @Query("SELECT * FROM $HEMISPHERIC_POWER_TABLE_NAME WHERE id IN (:)")
//    fun loadAllBySongId(vararg songIds: Int): List<Song?>?
//
//    @Query("SELECT * FROM song WHERE id IN (:songIds)")
//    fun loadAllBySongId(vararg songIds: Int): List<Song?>?
//
//    @Query("SELECT * FROM song WHERE name LIKE :name AND release_year = :year LIMIT 1")
//    fun loadOneByNameAndReleaseYear(first: String?, year: Int): Song?
//
//    @Insert
//    fun insertAll(vararg songs: Song?)
//
//    @Delete
//    fun delete(song: Song?)
