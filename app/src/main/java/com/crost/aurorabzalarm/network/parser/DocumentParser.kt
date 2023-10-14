package com.crost.aurorabzalarm.network.parser

import android.util.Log

class DocumentParser{
    private lateinit var dataTableMapped: MutableList<Map<String, String>>

    fun parseData(textDocument: String, valueNames: List<String>, valuesCount: Int): MutableList<Map<String, String>> {
        /**
         * Parses a structured text document and extracts information from the table.
         *
         * @param valuesCount Number of values per row to put the table in the right shape
         * @param textDocument Text document in a specific format (form https://services.swpc.noaa.gov/).
         *                     The document contains descriptions and table data separated by specific markers.
         *                     Format: Opening body tag, document content, and closing body tag.
         * @return table, where each row is a list of string values extracted from the table.
         * @throws ParseException if the input text document does not conform to the expected format.
         */
        val dataTable = extractDataTable(valuesCount, textDocument)
        dataTableMapped = mapParsedValuesToValueNames(dataTable, valueNames)
        return dataTableMapped
    }
    private fun extractDataTable(valuesCount: Int, textDocument: String): List<List<String>>{
        Log.d("DocumentParser", "document length start ${textDocument.length}")
        val table = mutableListOf<List<String>>()

        // cut off <body> tags by "\n" and split at "#". All data is behind last "#"
        val splitted = textDocument.split("\n")[1].split("#")
//        Log.d("splitted", splitted.toString())

//        Log.d("DocumentParser", "splitted: ${splitted[splitted.size-1]}")
        // take last element that only contains values and split at " " to get only string vals
        val splittedList = splitted[splitted.size-1].split(" ")
        // cut out first line cause it only contains "------- .. "
        val valuesOnly = splittedList.subList(1, splittedList.size-1)

        // calculate number of rows to get the table in the right shape
        val rowCount = valuesOnly.size / valuesCount
        Log.i("DocumentParser", "Number of rows: $rowCount, number of vals $valuesCount", )
        val size = valuesOnly.size
        for (i in 0..rowCount){
            val pos0 = i * valuesCount
            var pos1 = i * valuesCount + valuesCount
            if (pos1 >= size) {
                pos1 = size -1
            }
            val row = try {
                valuesOnly.subList(pos0, pos1)
            } catch (e: IndexOutOfBoundsException){
                Log.e("DocumentParser", "creating table\n ${e.printStackTrace()}")
                valuesOnly.subList(pos0, pos1-1)
            }
            Log.d("Row$i", row.toString())
            table.add(row)
        }
        return table
    }
}
