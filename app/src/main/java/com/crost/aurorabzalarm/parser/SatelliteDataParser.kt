package com.crost.aurorabzalarm.parser

import android.content.Context
import androidx.core.content.ContextCompat
import com.crost.aurorabzalarm.R

class SatelliteDataParser(
    context: Context,
) {
    private val urls = listOf(
        ContextCompat.getString(context, R.string.aceValsUrl),
        ContextCompat.getString(context, R.string.hpValsUrl)
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
    private val classesList = listOf(Float::class.java, Int::class.java)
    private val importantValues = listOf(
        mapOf(Pair("Bz", "nT")),
        mapOf(Pair("North-Hemispheric-Power-Index", "GW"))
    )

    private val parsers = urls.mapIndexed { index, url ->
        DataParser(valuesCountList[index], url, valueNamesList[index], importantValues[index], classesList[index])
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
        return success
    }
}