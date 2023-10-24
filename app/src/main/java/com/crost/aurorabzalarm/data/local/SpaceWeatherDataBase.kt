package com.crost.aurorabzalarm.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.crost.aurorabzalarm.Constants.EPAM_COL_DENSITY
import com.crost.aurorabzalarm.Constants.EPAM_COL_DT
import com.crost.aurorabzalarm.Constants.EPAM_COL_SPEED
import com.crost.aurorabzalarm.Constants.EPAM_COL_TEMP
import com.crost.aurorabzalarm.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData

@Database(
    entities = [
    AceMagnetometerData::class,
    HemisphericPowerData::class,
    AceEpamData::class
], version = 2, exportSchema = false
)


//@TypeConverters(Converters::class)
abstract class SpaceWeatherDataBase: RoomDatabase() {
    abstract fun aceDao(): AceMagnetometerDAO
    abstract fun hpDao(): HemisphericPowerDAO
    abstract fun epamDao(): AceEpamDAO

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



val migration1to2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            // Create the new table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS ${EPAM_TABLE_NAME} (" +
                        "${EPAM_COL_DT} LONG PRIMARY KEY NOT NULL, " +
                        "${EPAM_COL_DENSITY} DOUBLE, " +
                        "${EPAM_COL_SPEED} DOUBLE, " +
                        "${EPAM_COL_TEMP} DOUBLE)"
            )
        } catch (e: Exception) {
            Log.e("migration1to2", e.stackTraceToString())
        }

    }
}