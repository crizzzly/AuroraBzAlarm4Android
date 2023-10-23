package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.Constants.ACE_COL_BT
import com.crost.aurorabzalarm.Constants.ACE_COL_BX
import com.crost.aurorabzalarm.Constants.ACE_COL_BY
import com.crost.aurorabzalarm.Constants.ACE_COL_BZ
import com.crost.aurorabzalarm.Constants.ACE_COL_DT
import com.crost.aurorabzalarm.Constants.HP_COL_DT
import com.crost.aurorabzalarm.Constants.HP_COL_HPN
import com.crost.aurorabzalarm.Constants.HP_COL_HPS
import com.crost.aurorabzalarm.Constants.MAX_RETRY_COUNT
import com.crost.aurorabzalarm.Constants.RETRY_DELAY_MS
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
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
        Constants.ACE_TABLE_NAME -> {
            saveAceModelInstance(db, dataTable)
        }

        Constants.HP_TABLE_NAME -> {
            saveHpModelInstance(db, dataTable)
        }
    }
}



suspend fun getLatestAceValuesFromDb(db: SpaceWeatherDataBase): Any {
    val errorVal = -999.9
    Log.d("DatabaseOperator", "getLatestAceValuesFromDb")
    try {
        return db.aceDao().getLastRow()
    } catch (e: Exception) {
        Log.e("Repo getLatestValuesFromDb", e.stackTraceToString())
        delay(200)
        db.aceDao().getLastRow()
    }
    return "Error"
}


suspend fun getLatestHpValuesFromDb(db: SpaceWeatherDataBase): Any {
    Log.d("DatabaseOperator", "getLatestHpValuesFromDb")
    var retryCount = 0
    do {
        try {
            return db.hpDao().getLastRow()
        } catch (e: Exception) {
            Log.e("Repo getLatestHpValuesFromDb", e.stackTraceToString())
            retryCount ++
            delay(RETRY_DELAY_MS.toLong())
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

//suspend fun fetchLatestDataRow(db: SpaceWeatherDataBase): MutableList<Flow<Any>> {
//    val latestData = mutableListOf<Flow<Any>>()
//
//    try {
//        val aceData = getLatestAceValuesFromDb(db)
//
//        val hpData = getLatestHpValuesFromDb(db)
//
//        aceData.let {it as Flow<*>
//                latestData.add(it)
//                val data = it.collect()
//
////            latestData[ACE_COL_DT] = it.datetime
////            latestData[ACE_COL_BZ] = it.bz
//            Log.d("fetchLatestDataRow", "ace: ${it.collectLatest {  }}")
//        }
//
//        hpData.let {
//            latestData.add(it)
////            [HP_COL_DT] = it.datetime
////            latestData[HP_COL_HPN] = it.hpNorth
////            Log.d("fetchLatestDataRow", "hp: ${it.hpNorth}")
//        }
//    } catch (e: NullPointerException) {
////        latestData[ACE_COL_DT] = LocalDateTime.now()
////        latestData[ACE_COL_BZ] = -999.9
////        latestData[HP_COL_DT] = LocalDateTime.now()
////        latestData[HP_COL_HPN] = 0
//        throw e
//    }
//
//    return latestData
//}
//
