package com.crost.aurorabzalarm.parser

import android.util.Log

class DataParser<T>(
    valuesCount: Int,
    private val url: String,
    private val valueNames: List<String>,
    private val importantValues: Map<String, String>,
    private val clazz: Class<T>
) {
    var valuesTable = mutableListOf<MutableMap<String, T>>()

    private val dx = DataExtractor(valuesCount)

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
    ): MutableMap<String, T> {
        val mappedValues = mutableMapOf<String, T>()

        var index = 0
        for (value in stringVals) {
            if (value.isNotEmpty()) {
                mappedValues[valueNames[index]] = value as T
                index += 1
            }
        }

        // TODO: Save latest Bz value (in mappedValues) to CurrentSpaceWeatherData

        for (value in importantValues){
            Log.d("saveMappedAceValues", "${value.key}: ${mappedValues[value.key].toString()} ${value.value}")
        }
//        valuesTable.add(mappedValues)
        return mappedValues
    }
}
