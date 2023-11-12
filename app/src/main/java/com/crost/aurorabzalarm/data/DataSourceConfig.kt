package com.crost.aurorabzalarm.data

//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_URL
//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_URL
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ACE_JSON_5MINUTELY
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ALERTS_URL
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ENLIL_NAME
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.ENLIN_JSON
import com.crost.aurorabzalarm.utils.constants.DataSourceConstants.EPAM_JSON_5MINUTELY
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ACE_TABLE_NAME
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.ALERTS_PSEUDO_TABLE_NAME
import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.EPAM_TABLE_NAME

//import com.crost.aurorabzalarm.utils.constants.SpaceWeatherDataConstants.HP_URL

data class DataSourceConfig(
    val tableName: String,
    val url: String,
    val unit: String,
    var latestData: List<Any> = mutableListOf<Any>()
)


fun getDataSources(): List<DataSourceConfig> {
    val dataSources = mutableListOf<DataSourceConfig>()


    // TODO: Check which to drop
    val aceConfig = DataSourceConfig(
        tableName = ACE_TABLE_NAME,
        url = ACE_JSON_5MINUTELY,
        unit = "Nt"
    )
    dataSources.add(aceConfig)



    val epamConfig = DataSourceConfig(
        tableName = EPAM_TABLE_NAME,
        url = EPAM_JSON_5MINUTELY,
        unit = "p/cc km/s K"
    )
    dataSources.add(epamConfig)


    val alertSourceConfig = DataSourceConfig(
        tableName = ALERTS_PSEUDO_TABLE_NAME,
        url = ALERTS_URL,
        unit = ""
    )
    dataSources.add(alertSourceConfig)

    val enlilSourceConfig = DataSourceConfig(
        tableName = ENLIL_NAME,
        url = ENLIN_JSON,
        unit = ""
    )
    dataSources.add(enlilSourceConfig)

    return dataSources
}

