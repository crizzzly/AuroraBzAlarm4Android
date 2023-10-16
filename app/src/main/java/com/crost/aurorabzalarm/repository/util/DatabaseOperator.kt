package com.crost.aurorabzalarm.repository.util

import android.util.Log
import com.crost.aurorabzalarm.data.ParserConstants
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
    Log.d("addingDataModelInstances", tableName)
    when (tableName) {
        ParserConstants.ACE_TABLE_NAME -> {
            createAceModelInstance(db, dataTable)
        }

        ParserConstants.HP_TABLE_NAME -> {
            createHpModelInstance(db, dataTable)
        }
    }
}
suspend fun fetchLatestData(db: SpaceWeatherDataBase): MutableMap<String, Any> {
    val latestData = mutableMapOf<String, Any>()

    try {
        val aceData = getLatestAceValuesFromDb(db).lastOrNull()
        val hpData = getLatestHpValuesFromDb(db).lastOrNull()
        aceData?.let {
            latestData["datetime_ace"] = it.datetime
            latestData["bz"] = it.bz
        }

        hpData?.let {
            latestData["datetime_hp"] = it.datetime
            latestData["HpVal"] = it.hpNorth
        }
    } catch (e: NullPointerException) {
        latestData["datetime_ace"] = LocalDateTime.now()
        latestData["bz"] = -999.9
        latestData["datetime_hp"] = LocalDateTime.now()
        latestData["hpNorth"] = 0
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
            val datetime = row["datetime_ace"] as Long

            val aceDataModel = AceMagnetometerDataModel(
                datetime = datetime,
                bx = row["bx"] as Double,
                by = row["by"] as Double,
                bz = row["bz"] as Double,
                bt = row["bt"] as Double
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
        val datetime = row["datetime_hp"] as Long

        val hpDataModel = HemisphericPowerDataModel(
            datetime = datetime,
            hpNorth = row["hpNorth"] as Int,
            hpSouth = row["hpSouth"] as Int,
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