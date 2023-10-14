package com.crost.aurorabzalarm.network.parser.util.conversion

import com.crost.aurorabzalarm.data.constants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.repository.DataSourceConfig
import java.time.ZoneId

//import java.util.Date

class DataShaper() {
    // var dsConfig: DataSourceConfig, var dataTable: List<Map<String, String>>
    // var unitDatatypeMap = mutableMapOf<String, String>()
    private val localZoneId = ZoneId.systemDefault()
//    private lateinit var dsConfig: DataSourceConfig
//    private lateinit var dataTable: List<Map<String, String>>
    val aceConverter = AceDataConverter()
    fun convertData(dsConfig: DataSourceConfig, dataTable: List<Map<String, String>>): MutableList<MutableMap<String, Any>> {
//        dsConfig = dsConf
//        dataTable = dataT
        var converted = mutableListOf<MutableMap<String, Any>>()
        if (dsConfig.table_name == ACE_TABLE_NAME){
            converted = aceConverter.convertAceData(dataTable)
        }
        return converted
    }



}