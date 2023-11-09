package com.crost.aurorabzalarm.network.parser

import android.content.Context
import android.util.Log
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.utils.datetime_utils.convertUtcToLocal
import com.crost.aurorabzalarm.utils.datetime_utils.parseDateTimeString
import org.json.JSONArray
import java.time.LocalDateTime


const val DEBUG_DOCUMENT_SPLITTING = false
const val DEBUG_TABLE_SPLITTING = false


class DocumentParser{
    private lateinit var dataTableMapped: MutableList<Map<String, String>>

    fun parseData(context: Context, fileLogger: FileLogger, textDocument: String, valueNames: List<String>, valuesCount: Int): MutableList<Map<String, String>> {
        /**
         * Parses a structured text document and extracts information from the table.
         *
         * @param context
         * @param fileLogger
         * @param valuesCount Number of values per row to put the table in the right shape
         * @param textDocument Text document in a specific format (form https://services.swpc.noaa.gov/).
         *                     The document contains descriptions and table data separated by specific markers.
         *                     Format: Opening body tag, document content, and closing body tag.
         * @return table, where each row is a list of string values extracted from the table.
         * @throws ParseException if the input text document does not conform to the expected format.
         */
        val dataTable = extractDataTable(valuesCount, textDocument)
        dataTableMapped = mapParsedValuesToValueNames(context, dataTable, valueNames, fileLogger)
        return dataTableMapped
    }

    fun parseJson(jsonData: String): List<NoaaAlert> {
        val alerts = mutableListOf<NoaaAlert>()
        val jsonArray = JSONArray(jsonData)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val productId = jsonObject.getString("product_id")
            val dtString = jsonObject.getString("issue_datetime")
            val issueDatetime = dateTimeStringToLocalDateTime(dtString)
            val message = jsonObject.getString("message")
            val alert = NoaaAlert(productId, issueDatetime, message)
            alerts.add(alert)
        }

        return alerts
    }

    private fun dateTimeStringToLocalDateTime(dtString: String): LocalDateTime {
        val utc = parseDateTimeString(dtString)
        return convertUtcToLocal(utc)

    }


    private fun extractDataTable(valuesCount: Int, textDocument: String): List<List<String>>{
        val table = mutableListOf<List<String>>()

        // cut off <body> tags by "\n" and split at "#". All data is behind last "#"
        val splitted = textDocument.split("\n")[1].split("#")

        if(DEBUG_DOCUMENT_SPLITTING){
            for (text in splitted){
                Log.d("DocumentParser splitted", splitted.toString())
            }
        }

//        Log.d("DocumentParser", "splitted: ${splitted[splitted.size-1]}")
        // take last element that only contains values and split at " " to get only string vals
        val splittedList = splitted[splitted.size-1].split(" ")

//        Log.i("DocumentParser splittedList", "size: ${splittedList.size}")

        if(DEBUG_DOCUMENT_SPLITTING){
            var i = 0
            for (el in splittedList){
                Log.d("splittedList splitted by ' '", "Nr $i: $el")
                i += 1
        }
        }
        // cut out first line cause it only contains "------- .. "
        val valuesOnly = splittedList.subList(1, splittedList.size)

        if(DEBUG_TABLE_SPLITTING){
            var i = 0
            for (value in valuesOnly){
                Log.d("DocumentParser valuesOnly", "Nr $i: $value")
                i += 1
            }
        }
//        Log.d("DocumentParser", "valuesOnly: $valuesOnly")

        // calculate number of rows to get the table in the right shape
        val rowCount = valuesOnly.size / valuesCount
//        Log.i("DocumentParser", "Number of rows: $rowCount, number of vals $valuesCount", )
        val size = valuesOnly.size
        for (i in 0 until rowCount){
            val pos0 = i * valuesCount
            val pos1 = i * valuesCount + valuesCount

            val row = try {
                valuesOnly.subList(pos0, pos1)
            } catch (e: IndexOutOfBoundsException){
                Log.e("DocumentParser", "creating table\n ${e.printStackTrace()}")
                valuesOnly.subList(pos0, pos1-1)
            }

            if(DEBUG_TABLE_SPLITTING){
                Log.d("Row$i", row.toString() + "pos0: $pos0, pos1: $pos1")
            }
            table.add(row)
        }
        return table
    }
}
