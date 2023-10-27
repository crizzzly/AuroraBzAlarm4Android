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
], version = 1, exportSchema = false
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

val migration2to3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a temporary table to store existing data
        database.execSQL("CREATE TABLE IF NOT EXISTS temp_table (" +
                "$EPAM_COL_DT LONG PRIMARY KEY NOT NULL, " +
                "$EPAM_COL_DENSITY DOUBLE, " +
                "$EPAM_COL_SPEED DOUBLE, " +
                "$EPAM_COL_TEMP DOUBLE)"
        )

        // Copy data from the old table to the temporary table
        database.execSQL("INSERT INTO temp_table SELECT * FROM ace_swepam")

        // Drop the old table
        database.execSQL("DROP TABLE IF EXISTS ace_swepam")

        // Create the new table (with the modified schema)
        database.execSQL("CREATE TABLE IF NOT EXISTS $EPAM_TABLE_NAME (" +
                "$EPAM_COL_DT LONG PRIMARY KEY NOT NULL, " +
                "$EPAM_COL_DENSITY DOUBLE, " +
                "$EPAM_COL_SPEED DOUBLE, " +
                "$EPAM_COL_TEMP DOUBLE)")

        // Copy data from the temporary table to the new table
        database.execSQL("INSERT INTO $EPAM_TABLE_NAME SELECT * FROM temp_table")

        // Drop the temporary table
        database.execSQL("DROP TABLE IF EXISTS temp_table")

        // Perform any additional operations, such as adding indexes or triggers
        // For example, if you need to execute SQL statements, you can use database.execSQL("YOUR_SQL_STATEMENT_HERE");

        // Perform the necessary database migration operations here
        // For example, if you need to execute SQL statements, you can use database.execSQL("YOUR_SQL_STATEMENT_HERE");
    }
}




val migration1to2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            // Create the new table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS $EPAM_TABLE_NAME (" +
                        "$EPAM_COL_DT LONG PRIMARY KEY NOT NULL, " +
                        "$EPAM_COL_DENSITY DOUBLE, " +
                        "$EPAM_COL_SPEED DOUBLE, " +
                        "$EPAM_COL_TEMP DOUBLE)"
            )
        } catch (e: Exception) {
            Log.e("migration1to2", e.stackTraceToString())
        }

    }
}


val migration1to3 = object : Migration(1, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            // Create the new table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS $EPAM_TABLE_NAME (" +
                        "$EPAM_COL_DT LONG PRIMARY KEY NOT NULL, " +
                        "$EPAM_COL_DENSITY DOUBLE, " +
                        "$EPAM_COL_SPEED DOUBLE, " +
                        "$EPAM_COL_TEMP DOUBLE)"
            )
        } catch (e: Exception) {
            Log.e("migration1to2", e.stackTraceToString())
        }

    }
}