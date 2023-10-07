package com.crost.aurorabzalarm.data

class SatelliteDataParser() {
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
    private val importantValues = listOf(
        mapOf(Pair("Bz", "nT")),
        mapOf(Pair("North-Hemispheric-Power-Index", "GW"))
    )

    private val parsers = urls.mapIndexed { index, url ->
        DataParser(valuesCountList[index], url, valueNamesList[index], importantValues[index])
    }

    private var latestValues = mutableListOf<Float>()

    fun parseSatelliteData(): List<Float> {
        var success = true
        for (index in urls.indices) {
            val parser = parsers[index]

            val result = parser.parseSatelliteData()
            if (!result) {
                success = false
                // Handle the failure, if necessary
            }
        }
        updateCurrentSpaceWeatherData()
        return latestValues
    }

    private fun updateCurrentSpaceWeatherData(){
        latestValues = mutableListOf()
        parsers.forEach(){  dataParser ->
            latestValues.add(dataParser.value)
        }
    //        _currentSpaceWeather.value.bzVal = newVals[0]
//        _currentSpaceWeather.value.hemisphericPower = newVals[1]
    }
}