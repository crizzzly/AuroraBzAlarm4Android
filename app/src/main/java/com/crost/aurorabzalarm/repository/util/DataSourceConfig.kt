package com.crost.aurorabzalarm.repository.util

import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.ACE_URL
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.Constants.HP_URL

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

    val aceConfig = DataSourceConfig(
        tableName = ACE_TABLE_NAME,
        url = ACE_URL,
        keys = aceKeys,
        unit = "Nt"
    )
    val hpConfig = DataSourceConfig(
        tableName = HP_TABLE_NAME,
        url = HP_URL,
        keys = hpKeys,
        unit = "GW"
    )
    return setOf(aceConfig, hpConfig).toList()
}

