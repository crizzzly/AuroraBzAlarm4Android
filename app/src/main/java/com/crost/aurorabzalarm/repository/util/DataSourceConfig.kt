package com.crost.aurorabzalarm.repository.util

import com.crost.aurorabzalarm.utils.Constants
import com.crost.aurorabzalarm.utils.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.ACE_URL
import com.crost.aurorabzalarm.utils.Constants.EPAM_KEYS
import com.crost.aurorabzalarm.utils.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.EPAM_URL
import com.crost.aurorabzalarm.utils.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.utils.Constants.HP_URL

data class DataSourceConfig(
    val tableName: String,
    val url: String,
    val keys: List<String>,
    val unit: String,
    var latestData: MutableList<MutableMap<String, Any>> =
        mutableListOf<MutableMap<String, Any>>()
)


fun getDataSources(): List<DataSourceConfig> {
    val aceKeys = Constants.ACE_KEYS.split(" ")
    val hpKeys = Constants.HP_KEYS.split(" ")
    val epamKeys = EPAM_KEYS.split(" ")

    var dataSources = mutableListOf<DataSourceConfig>()


    val aceConfig = DataSourceConfig(
        tableName = ACE_TABLE_NAME,
        url = ACE_URL,
        keys = aceKeys,
        unit = "Nt"
    )
    dataSources.add(aceConfig)

    
    val hpConfig = DataSourceConfig(
        tableName = HP_TABLE_NAME,
        url = HP_URL,
        keys = hpKeys,
        unit = "GW"
    )
    dataSources.add(hpConfig)

    val epamConfig = DataSourceConfig(
        tableName = EPAM_TABLE_NAME,
        url = EPAM_URL,
        keys = epamKeys,
        unit = "p/cc km/s K"
    )
    dataSources.add(epamConfig)

    return dataSources
}

