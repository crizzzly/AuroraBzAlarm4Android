package com.crost.aurorabzalarm.data

import android.util.Log

class DataParser(
    valuesCount: Int,
    private val url: String,
    private val valueNames: List<String>,
    private val importantValues: Map<String, String>,
) {
    private var valuesTable = mutableListOf<MutableMap<String, String>>()

    private val dx = DataExtractor(valuesCount)
    var value: Float = 0.0f

    fun parseSatelliteData(): Boolean {
        val dataRows = dx.getSatelliteData(url)
        return if (dataRows.isNotEmpty()) {
            dataRows.forEach { row ->
                val mappedValues = mapValues(row)
                valuesTable.add(mappedValues)
            }
            true
        } else {
            false
        }
    }


    private fun mapValues(
         stringVals: List<String>,
    ): MutableMap<String, String> {
        val mappedValues = mutableMapOf<String, String>()

        var index = 0
        for (value in stringVals) {
            if (value.isNotEmpty()) {
                mappedValues[valueNames[index]] = value
                index += 1
            }
        }

        // TODO: Save latest Bz value (in mappedValues) to CurrentSpaceWeatherData

        for (v in importantValues){
            Log.d("saveMappedAceValues", "${v.key}: ${mappedValues[v.key].toString()} ${v.value}")
            value = mappedValues[v.key]?.toFloat()!!
        }

//        valuesTable.add(mappedValues)
        return mappedValues
    }
}
