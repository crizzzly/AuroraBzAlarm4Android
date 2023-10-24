package com.crost.aurorabzalarm.network.parser.util.conversion

import com.crost.aurorabzalarm.Constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.Constants.EPAM_TABLE_NAME
import com.crost.aurorabzalarm.Constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.repository.util.DataSourceConfig


class DataShaper {
    fun convertData(dsConfig: DataSourceConfig, dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        return when (dsConfig.tableName) {
            ACE_TABLE_NAME -> AceDataConverter().convertAceData(dataTable)
            HP_TABLE_NAME -> HpDataConverter().convertHpData(dataTable)
            EPAM_TABLE_NAME -> EpamDataConverter().convertEpamData(dataTable)
            else -> mutableListOf()
        }
    }
}