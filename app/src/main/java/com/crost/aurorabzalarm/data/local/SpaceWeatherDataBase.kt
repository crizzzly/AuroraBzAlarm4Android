package com.crost.aurorabzalarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData

@Database(entities = [AceMagnetometerData::class, HemisphericPowerData::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class SpaceWeatherDataBase: RoomDatabase() {
    abstract fun aceDao(): AceMagnetometerDAO
    abstract fun hpDao(): HemisphericPowerDAO

    companion object {
        @Volatile
        private var INSTANCE: SpaceWeatherDataBase? = null
        fun getDatabase(context: Context): SpaceWeatherDataBase
        {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    SpaceWeatherDataBase::class.java,
                    "spaceweather_database")
//                    .createFromAsset("database/bus_schedule.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }

}