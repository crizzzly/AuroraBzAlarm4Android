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
import com.crost.aurorabzalarm.data.local.SpaceWeatherDataBase
import com.crost.aurorabzalarm.data.model.AceMagnetometerDataModel
import com.crost.aurorabzalarm.data.model.HemisphericPowerDataModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import java.time.LocalDateTime


suspend fun addDataModelInstances(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>,
    tableName: String,
) {
    Log.d("addingInstances", tableName)
    when (tableName) {
        Constants.ACE_TABLE_NAME -> {
            createAceModelInstance(db, dataTable)
        }

        Constants.HP_TABLE_NAME -> {
            createHpModelInstance(db, dataTable)
        }
    }
}


suspend fun fetchLatestDataRow(db: SpaceWeatherDataBase): MutableMap<String, Any> {
    val latestData = mutableMapOf<String, Any>()

    try {
        val aceData = getLatestAceValuesFromDb(db).lastOrNull()
        val hpData = getLatestHpValuesFromDb(db).lastOrNull()
        aceData?.let {
            latestData[ACE_COL_DT] = it.datetime
            latestData[ACE_COL_BZ] = it.bz
        }

        hpData?.let {
            latestData[HP_COL_DT] = it.datetime
            latestData[HP_COL_HPN] = it.hpNorth
        }
    } catch (e: NullPointerException) {
        latestData[ACE_COL_DT] = LocalDateTime.now()
        latestData[ACE_COL_BZ] = -999.9
        latestData[HP_COL_DT] = LocalDateTime.now()
        latestData[HP_COL_HPN] = 0
    }

    return latestData
}


suspend fun createAceModelInstance(
    db: SpaceWeatherDataBase,
    dataTable: MutableList<MutableMap<String, Any>>
) {
    val instances = mutableListOf<AceMagnetometerDataModel>()

        // the loop gets executed exactly once until execution stops without error
        for (row in dataTable) {
            val datetime = row[ACE_COL_DT] as Long

            val aceDataModel = AceMagnetometerDataModel(
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


suspend fun createHpModelInstance(db: SpaceWeatherDataBase, dataTable: MutableList<MutableMap<String, Any>>) {
//        Log.d("createHpModelInstance", "createHpModelInstance")
    val instances = mutableListOf<HemisphericPowerDataModel>()

    for (row in dataTable) {
        val datetime = row[HP_COL_DT] as Long

        val hpDataModel = HemisphericPowerDataModel(
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


fun getLatestAceValuesFromDb(db: SpaceWeatherDataBase): Flow<AceMagnetometerDataModel> {
    return try {
        db.aceDao().getLastRow()
    } catch (e: Exception) {
        Log.e("Repo getLatestValuesFromDb", e.stackTraceToString())
        //                delay(200)
        db.aceDao().getLastRow()
    }
}


suspend fun getLatestHpValuesFromDb(db: SpaceWeatherDataBase): Flow<HemisphericPowerDataModel> {
    return try {
        db.hpDao().getLastRow()
    } catch (e: Exception) {
        Log.e("Repo getLatestHpValuesFromDb", e.stackTraceToString())
//                        delay(200)
        db.hpDao().getLastRow()
    }
}