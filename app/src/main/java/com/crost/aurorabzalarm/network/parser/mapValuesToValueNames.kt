package com.crost.aurorabzalarm.network.parser

import android.util.Log


fun mapParsedValuesToValueNames(
    dataTable: List<List<String>>,
    valueNames: List<String>,
): MutableList<Map<String, String>> {

    val mappedValueTable = mutableListOf<Map<String, String>>()
    var index = 0
    for (row in dataTable) {
        val mappedValues = mutableMapOf<String, String>()
        for (value in row) {
            try {
                if (value.isNotEmpty()) {
                    mappedValues[valueNames[index]] = value
                    index += 1
                }
            } catch (e: IndexOutOfBoundsException){
                Log.e("mapParsedValuesToValueNames", "index: $index\n" +
                        e.printStackTrace())
            }
        }
        mappedValueTable.add(mappedValues)
    }
    return mappedValueTable
}

