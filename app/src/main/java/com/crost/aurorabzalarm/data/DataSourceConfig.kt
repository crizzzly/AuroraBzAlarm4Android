package com.crost.aurorabzalarm.data

//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_URL
//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_URL
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ACE_JSON_5MINUTELY
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ALERTS_URL
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.EPAM_JSON_5MINUTELY
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_KEYS
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_TABLE_NAME

//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.HP_URL

data class DataSourceConfig(
    val tableName: String,
    val url: String,
    val keys: List<String>,
    val unit: String,
    var latestData: List<Any> = mutableListOf<Any>()
)


fun getDataSources(): List<DataSourceConfig> {
    val aceKeys = SpaceWeatherDataConstants.ACE_KEYS.split(" ")
    val epamKeys = EPAM_KEYS.split(" ")

    val dataSources = mutableListOf<DataSourceConfig>()


    // TODO: Check which to drop
    val aceConfig = DataSourceConfig(
        tableName = ACE_TABLE_NAME,
        url = ACE_JSON_5MINUTELY,
        keys = aceKeys,
        unit = "Nt"
    )
    dataSources.add(aceConfig)



    val epamConfig = DataSourceConfig(
        tableName = EPAM_TABLE_NAME,
        url = EPAM_JSON_5MINUTELY,
        keys = epamKeys,
        unit = "p/cc km/s K"
    )
    dataSources.add(epamConfig)


    val alertSourceConfig = DataSourceConfig(
        tableName = ALERTS_PSEUDO_TABLE_NAME,
        url = ALERTS_URL,
        keys = emptyList<String>(),
        unit = ""
    )
    dataSources.add(alertSourceConfig)

    return dataSources
}

