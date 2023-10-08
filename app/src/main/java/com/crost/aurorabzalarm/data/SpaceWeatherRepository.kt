package com.crost.aurorabzalarm.data

import android.util.Log
import com.crost.aurorabzalarm.ui.SpaceWeatherState


class SpaceWeatherRepository() {
     private val satelliteDataParser = SatelliteDataParser()
    fun fetchData(): SpaceWeatherState {
        // Use satelliteDataParser to fetch and parse data
        val parsedData = satelliteDataParser.parseSatelliteData()
        Log.d("SpaceWeatherRepository", "getData: ${parsedData[0]}")
            return returnSpaceWeatherState(parsedData)
    }

    private fun returnSpaceWeatherState(data: List<Float>?): SpaceWeatherState {
//        Log.d("returnSpaceWeatherState", data.toString())
        val bzVal = data!![0]
        val hpVal = data[1]
        return SpaceWeatherState(bzVal, hpVal)
    }
}