package com.crost.aurorabzalarm.network.parser.util.conversion

import com.crost.aurorabzalarm.data.ParserConstants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.ParserConstants.HP_TABLE_NAME
import com.crost.aurorabzalarm.repository.DataSourceConfig


class DataShaper {
    fun convertData(dsConfig: DataSourceConfig, dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        return when (dsConfig.table_name) {
            ACE_TABLE_NAME -> AceDataConverter().convertAceData(dataTable)
            HP_TABLE_NAME -> HpDataConverter().convertHpData(dataTable)
            else -> mutableListOf()
        }
    }
}