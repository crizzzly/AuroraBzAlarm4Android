package com.crost.aurorabzalarm.data

import android.util.Log
import com.crost.aurorabzalarm.ui.SpaceWeatherState


class SpaceWeatherRepository() {
     private val satelliteDataParser = SatelliteDataParser()
    fun fetchData(): SpaceWeatherState {
        // Use satelliteDataParser to fetch and parse data
        val parsedData = satelliteDataParser.parseSatelliteData()
        Log.d("SpaceWeatherRepository", "getData: ${parsedData.entries.toString()}")
            return SpaceWeatherState(parsedData)
    }

//    private fun returnSpaceWeatherState(data: Map<String, Any>): SpaceWeatherState {
////        Log.d("returnSpaceWeatherState", data.toString())
//        val bzVal = data!![0]
//        val hpVal = data[1]
//        return SpaceWeatherState(bzVal, hpVal)
//    }
}