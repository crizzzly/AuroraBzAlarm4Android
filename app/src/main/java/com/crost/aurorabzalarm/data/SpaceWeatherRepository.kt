package com.crost.aurorabzalarm.data

import android.util.Log
import com.crost.aurorabzalarm.CurrentSpaceWeatherState
import com.crost.aurorabzalarm.SpaceWeatherState

class SpaceWeatherRepository(private val satelliteDataParser: SatelliteDataParser) {
    fun getSpaceWeatherData(): CurrentSpaceWeatherState {
        // Use satelliteDataParser to fetch and parse data
        val parsedData = satelliteDataParser.parseSatelliteData()
        Log.d("SpaceWeatherRepository", "getData: ${parsedData[0]}")
        val bzVal = parsedData[0]
        val hpVal = parsedData[1]
        return SpaceWeatherState(bzVal, hpVal)
    }
}