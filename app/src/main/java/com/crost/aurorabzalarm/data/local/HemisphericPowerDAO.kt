package com.crost.aurorabzalarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crost.aurorabzalarm.data.constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.model.HemisphericPowerDataModel


@Dao
interface HemisphericPowerDAO {
    @Query("SELECT * FROM $HP_TABLE_NAME")
    fun getAllData(): List<HemisphericPowerDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataRow(data: HemisphericPowerDataModel)

    @Insert
    fun insertAll(vararg hpData: HemisphericPowerDataModel?)


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
}