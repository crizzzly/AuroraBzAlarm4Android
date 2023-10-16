package com.crost.aurorabzalarm.repository.util

import com.crost.aurorabzalarm.Constants
import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.ACE_URL
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.Constants.HP_URL

data class DataSourceConfig(
    val table_name: String,
    val url: String,
    val keys: List<String>,
    val unit: String
)


fun getDataSources(): List<DataSourceConfig> {
    val aceKeys = Constants.ACE_KEYS.split(" ")
    val hpKeys = Constants.HP_KEYS.split(" ")

    val aceConfig = DataSourceConfig(
        table_name = ACE_TABLE_NAME,
        url = ACE_URL,
        keys = aceKeys,
        unit = "Nt"
    )
    val hpConfig = DataSourceConfig(
        table_name = HP_TABLE_NAME,
        url = HP_URL,
        keys = hpKeys,
        unit = "GW"
    )
    return setOf(aceConfig, hpConfig).toList()
}

