package com.crost.aurorabzalarm.data

import android.util.Log
import com.crost.aurorabzalarm.DataViewModel

class SatelliteDataParser(private val viewModel: DataViewModel) {
//    private var _currentSpaceWeather = mutableStateOf(CurrentSpaceWeatherState())
//    val currentSpaceWeather: State<CurrentSpaceWeatherState>
//        get() = _currentSpaceWeather


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

    fun parseSatelliteData(): Boolean {
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
        return success
    }

    private fun updateCurrentSpaceWeatherData(){
        val newVals = mutableListOf<Float>()
        parsers.forEach(){  dataParser ->
            newVals.add(dataParser.value)
        }
        Log.d("updateCurrentSpaceWeatherData", "Bz: ${newVals[0]} nT, Hp: ${newVals[1]}GW")
        viewModel.updateSpaceWeatherState(newVals)
    //        _currentSpaceWeather.value.bzVal = newVals[0]
//        _currentSpaceWeather.value.hemisphericPower = newVals[1]
    }
}