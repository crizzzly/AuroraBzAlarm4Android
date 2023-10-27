package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.Constants.ACE_COL_BT
import com.crost.aurorabzalarm.Constants.ACE_COL_BX
import com.crost.aurorabzalarm.Constants.ACE_COL_BY
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_COL_DT
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.EPAM_COL_DENSITY
import com.crost.aurorabzalarm.Constants.EPAM_COL_DT
import com.crost.aurorabzalarm.Constants.EPAM_COL_SPEED
import com.crost.aurorabzalarm.Constants.EPAM_COL_TEMP
import com.crost.aurorabzalarm.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.Constants.HP_COL_DT
import com.crost.aurorabzalarm.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.Constants.HP_COL_HPS
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.Constants.RETRY_DELAY_MS
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceEpamData
import com.crost.aurorabzalarm.data.model.AceMagnetometerData
import com.crost.aurorabzalarm.data.model.HemisphericPowerData
import kotlinx.coroutines.delay


suspend fun saveDataModelInstances(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>,
    tableName: String,
) {
    Log.d("addingInstances", tableName)
    when (tableName) {
        ACE_TABLE_NAME -> {
            saveAceModelInstance(db, dataTable)
        }

        HP_TABLE_NAME -> {
            saveHpModelInstance(db, dataTable)
        }

        EPAM_TABLE_NAME -> {
            saveEpamModelInstance(db, dataTable)
        }
    }
}



suspend fun getLatestAceValuesFromDb(db: SpaceWeatherDataBase): Any {
    Log.d("DatabaseOperator", "getLatestAceValuesFromDb")
    var retryCount = 0

    do {
        try {
            val data = db.aceDao().getLastRow()
            Log.d("getLatestAceValuesFromDb", "successfully got latest datarow")
            return data
        } catch (e: Exception) {
            retryCount += 1
            Log.e("getLatestAceValuesFromDb", e.stackTraceToString())
            delay(RETRY_DELAY_MS)
        }
    } while(retryCount < MAX_RETRY_COUNT)
        return "Error"

}


suspend fun getLatestHpValuesFromDb(db: SpaceWeatherDataBase): Any {
    Log.d("DatabaseOperator", "getLatestHpValuesFromDb")
    var retryCount = 0
    do {
        try {
            val data = db.hpDao().getLastRow()
            Log.d("getLatestHpValuesFromDb", "successfully loaded latest data row")
            return data

        } catch (e: Exception) {
            Log.e("getLatestHpValuesFromDb", e.stackTraceToString())
            retryCount ++
            delay(RETRY_DELAY_MS)
        }
    } while (retryCount < MAX_RETRY_COUNT)
    return "Error"
}

suspend fun getLatestEpamValuesFromDb(db: SpaceWeatherDataBase): Any {
    Log.d("DatabaseOperator", "getLatestEpamValuesFromDb")
    var retryCount = 0
    do {
        try {
            val data = db.epamDao().getLastRow()
            Log.d("getLatestEpamValuesFromDb", "successfully loaded latest data row")

            return data
        } catch (e: Exception) {
            Log.e(
                "getLatestEpamValuesFromDb",
                "Retry No $retryCount ${e.stackTraceToString()}"
            )
            retryCount ++
            delay(RETRY_DELAY_MS)
        }
    } while (retryCount < MAX_RETRY_COUNT)
    return "Error"
}

suspend fun saveAceModelInstance(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>
) {
    val instances = mutableListOf<AceMagnetometerData>()

    // the loop gets executed exactly once until execution stops without error
    for (row in dataTable) {
        val datetime = row[ACE_COL_DT] as Long

        val aceDataModel = AceMagnetometerData(
            datetime = datetime,
            bx = row[ACE_COL_BX] as Double,
            by = row[ACE_COL_BY] as Double,
            bz = row[ACE_COL_BZ] as Double,
            bt = row[ACE_COL_BT] as Double
        )
        instances.add(aceDataModel)
    }
    try {
        // Add new rows to the db
        db.aceDao().insertAll(instances)

    } catch (e: Exception) {
        Log.e("SpaceWeatherRepo createAceInstance", e.stackTraceToString())
        db.aceDao().insertAll(instances)
    }
}


suspend fun saveHpModelInstance(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>
) {
//        Log.d("createHpModelInstance", "createHpModelInstance")
    val instances = mutableListOf<HemisphericPowerData>()

    for (row in dataTable) {
        val datetime = row[HP_COL_DT] as Long

        val hpDataModel = HemisphericPowerData(
            datetime = datetime,
            hpNorth = row[HP_COL_HPN] as Int,
            hpSouth = row[HP_COL_HPS] as Int,
        )
        instances.add(hpDataModel)
    }

    try {
        // Add new rows to the db
        db.hpDao().insertAll(instances)
    } catch (e: Exception) {
        Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
        delay(200)
        db.hpDao().insertAll(instances)
    }
}

suspend fun saveEpamModelInstance(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>
) {
//        Log.d("createHpModelInstance", "createHpModelInstance")
    val instances = mutableListOf<AceEpamData>()

    for (row in dataTable) {
        val datetime = row[EPAM_COL_DT] as Long

        val epamDataModel = AceEpamData(
            datetime = datetime,
            density = row[EPAM_COL_DENSITY] as Double,
            speed = row[EPAM_COL_SPEED] as Double,
            temp = row[EPAM_COL_TEMP] as Double,
        )
        instances.add(epamDataModel)
    }

    try {
        // Add new rows to the db
        db.epamDao().insertAll(instances)
    } catch (e: Exception) {
        Log.e("SpaceWeatherRepo createHpInstance", e.stackTraceToString())
        delay(200)
        db.epamDao().insertAll(instances)
    }
}
