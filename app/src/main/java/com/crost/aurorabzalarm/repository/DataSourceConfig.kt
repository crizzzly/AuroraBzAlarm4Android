package com.crost.aurorabzalarm.repository

import com.crost.aurorabzalarm.data.constants
import com.crost.aurorabzalarm.data.constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.constants.ACE_URL
import com.crost.aurorabzalarm.data.constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.data.constants.HP_URL

data class DataSourceConfig(
    val table_name: String,
    val url: String,
    val keys: List<String>,
    val unit: String
)


fun getDataSources(): Set<DataSourceConfig> {
    lateinit var dataSourceConfig: DataSourceConfig
    val aceKeys = constants.ACE_KEYS.split(" ")
    val hpKeys = constants.HP_KEYS.split(" ")

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
    return setOf(aceConfig, hpConfig)
}

