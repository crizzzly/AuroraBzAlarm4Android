package com.crost.aurorabzalarm.network.parser.util.conversion

import com.crost.aurorabzalarm.data.constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.data.constants.HP_TABLE_NAME
import com.crost.aurorabzalarm.repository.DataSourceConfig


class DataShaper {
    private val aceConverter = AceDataConverter()
    private val hpConverter = HpDataConverter()
    fun convertData(dsConfig: DataSourceConfig, dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
        var converted = mutableListOf<MutableMap<String, Any>>()
        if (dsConfig.table_name == ACE_TABLE_NAME){
            converted = aceConverter.convertAceData(dataTable)
        }
        else if (dsConfig.table_name == HP_TABLE_NAME){
            converted = hpConverter.convertHpData(dataTable)
        }
        return converted
    }



}