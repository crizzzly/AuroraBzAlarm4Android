package com.crost.aurorabzalarm.network.parser

import android.util.Log


fun mapParsedValuesToValueNames(
    dataTable: List<List<String>>,
    valueNames: List<String>,
): MutableList<Map<String, String>> {

    val mappedValueTable = mutableListOf<Map<String, String>>()
    for (row in dataTable) {
        var index = 0

        val mappedValues = mutableMapOf<String, String>()
        for (value in row) {
            try {
                if (value.isNotEmpty()) {
                    mappedValues[valueNames[index]] = value
                }
            } catch (e: IndexOutOfBoundsException){
                Log.e("mapParsedValuesToValueNames", "index: $index, value: $value\n" +
                        e.printStackTrace())
            }
            index += 1
        }
        mappedValueTable.add(mappedValues)
    }
    return mappedValueTable
}

