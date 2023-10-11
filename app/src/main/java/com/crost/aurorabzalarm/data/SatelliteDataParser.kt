package com.crost.aurorabzalarm.data

class SatelliteDataParser {
    private val urls = listOf(
        "https://services.swpc.noaa.gov/text/ace-magnetometer.txt",//ContextCompat.getString(context, R.string.aceValsUrl),
        "https://services.swpc.noaa.gov/text/aurora-nowcast-hemi-power.txt"//ContextCompat.getString(context, R.string.hpValsUrl)
    )

    // TODO: There's a better way to handle that!
    private val valueNamesList = listOf(
        listOf(
            "YR", "MO", "DA", "Time", "JulianDay", "SecOfDay", "S",
            "Bx", "By", "Bz", "Bt", "Lat", "Long"
        ),
        listOf("Observation", "Forecast", "North-Hemispheric-Power-Index", "South-Hemispheric-Power-Index")
    )

    private val valuesCountList = listOf(13, 4)
    // TODO: should be string resource 
    private val importantValues = listOf(
        mapOf(Pair("Bz", "nT")),
        mapOf(Pair("North-Hemispheric-Power-Index", "GW"))
    )

    private val managers = urls.mapIndexed { index, url ->
        SatelliteDataManager(valuesCountList[index], url, valueNamesList[index], importantValues[index])
    }

    private var latestValues = mutableMapOf<String, Any>()

    fun parseSatelliteData(): MutableMap<String, Any> {
        for (index in urls.indices) {
            val parser = managers[index]

            val result = parser.getDataTable()
            if (!result) {
                // Handle the failure, if necessary
            }
        }
        updateLatestValues()
        return  latestValues
    }

    private fun updateLatestValues(){
        latestValues = mutableMapOf()
        managers.forEach { data ->
            latestValues[data.key] = data.value
        }
    }
}