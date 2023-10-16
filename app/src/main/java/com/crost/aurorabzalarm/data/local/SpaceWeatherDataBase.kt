package com.crost.aurorabzalarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crost.aurorabzalarm.data.model.AceMagnetometerDataModel
import com.crost.aurorabzalarm.data.model.HemisphericPowerDataModel

@Database(entities = [AceMagnetometerDataModel::class, HemisphericPowerDataModel::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class SpaceWeatherDataBase: RoomDatabase() {
    abstract fun aceDao(): AceMagnetometerDAO
    abstract fun hpDao(): HemisphericPowerDAO

}