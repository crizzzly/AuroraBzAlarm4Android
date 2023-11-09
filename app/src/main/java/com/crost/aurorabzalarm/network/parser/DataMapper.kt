package com.crost.aurorabzalarm.network.parser

import android.content.Context
import android.util.Log
import com.crost.aurorabzalarm.utils.FileLogger


fun mapParsedValuesToValueNames(
    context: Context,
    dataTable: List<List<String>>,
    valueNames: List<String>,
    fileLogger: FileLogger
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
                val msg = "index: $index, value: $value\n" + e.stackTraceToString()
                fileLogger.writeLogsToInternalStorage(
                    context,
                    "mapParsedValuesToValueNames" +
                    msg
                )
                Log.e("mapParsedValuesToValueNames", msg)
            }
            index += 1
        }
        mappedValueTable.add(mappedValues)
    }
//    Log.d("MapValues", "value mapping of ${valueNames.size} Values completed")
    return mappedValueTable
}
