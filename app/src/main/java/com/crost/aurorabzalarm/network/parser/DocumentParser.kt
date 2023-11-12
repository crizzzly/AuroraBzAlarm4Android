package com.crost.aurorabzalarm.network.parser

import android.util.Log
import com.crost.aurorabzalarm.data.ImfData
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.SolarWindData
import com.crost.aurorabzalarm.utils.datetime_utils.convertUtcToLocal
import com.crost.aurorabzalarm.utils.datetime_utils.parseDateTimeString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.time.LocalDateTime


const val DEBUG_DOCUMENT_SPLITTING = false
const val DEBUG_TABLE_SPLITTING = false
const val DEBUG_LIST = false


class DocumentParser {
    private lateinit var dataTableMapped: MutableList<Map<String, String>>

    fun parseSolarWindJson(jsonData: String): List<Any> {
        val dataList = convertListStringToList(jsonData)
        // Map the arrays to DataEntry objects
        val dataEntries = dataList.drop(1).map { entry ->
            SolarWindData(
                dateTime = dateTimeStringToLocalDateTime(entry[0]),// ,
                density = entry[1].toDouble(),
                speed = entry[2].toDouble(),
                temperature = entry[3].toDouble(),
            )
        }
        return dataEntries
    }


    fun parseIMFJson(jsonData: String): List<ImfData> {
        val dataList: List<List<String>> =
            convertListStringToList(jsonData)
        // Map the arrays to DataEntry objects
        val dataEntries = dataList.drop(1).map { entry ->
            if (DEBUG_LIST) Log.d("parseIMFJson", entry.toString())
            ImfData(
                dateTime = dateTimeStringToLocalDateTime(entry[0]),// ,
                bx = entry[1].toDouble(),
                by = entry[2].toDouble(),
                bz = entry[3].toDouble(),
                bt = entry[4].toDouble()
            )
        }

        return dataEntries
    }

    private fun convertListStringToList(jsonData: String): List<List<String>> {
        val tmp = jsonData.trimIndent()
        if (DEBUG_LIST) Log.d("parseIMF/ACEJson", "tmp: $tmp")
        val listType = object : TypeToken<List<List<String>>>() {}.type

        return Gson().fromJson(tmp, listType)
    }

    fun parseAlertJson(jsonData: String): List<NoaaAlert> {
        /**
         * parses alerts from jsonData String from
         * https://services.swpc.noaa.gov/products/alerts.json
         * converts issueDateTime (String - UTC) to LocalDateTime
         * puts values in list of NoaaAlert data class
         *
         * @param jsonData: Alerts from noaa as json
         * @return: List<NoaaAlert>
         */

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
}

//    private fun extractDataTable(valuesCount: Int, textDocument: String): List<List<String>>{
//        val table = mutableListOf<List<String>>()
//
//        // cut off <body> tags by "\n" and split at "#". All data is behind last "#"
//        val splitted = textDocument.split("\n")[1].split("#")
//
//        if(DEBUG_DOCUMENT_SPLITTING){
//            for (text in splitted){
//                Log.d("DocumentParser splitted", splitted.toString())
//            }
//        }
//
////        Log.d("DocumentParser", "splitted: ${splitted[splitted.size-1]}")
//        // take last element that only contains values and split at " " to get only string vals
//        val splittedList = splitted[splitted.size-1].split(" ")
//
////        Log.i("DocumentParser splittedList", "size: ${splittedList.size}")
//
//        if(DEBUG_DOCUMENT_SPLITTING){
//            var i = 0
//            for (el in splittedList){
//                Log.d("splittedList splitted by ' '", "Nr $i: $el")
//                i += 1
//        }
//        }
//        // cut out first line cause it only contains "------- .. "
//        val valuesOnly = splittedList.subList(1, splittedList.size)
//
//        if(DEBUG_TABLE_SPLITTING){
//            var i = 0
//            for (value in valuesOnly){
//                Log.d("DocumentParser valuesOnly", "Nr $i: $value")
//                i += 1
//            }
//        }
////        Log.d("DocumentParser", "valuesOnly: $valuesOnly")
//
//        // calculate number of rows to get the table in the right shape
//        val rowCount = valuesOnly.size / valuesCount
////        Log.i("DocumentParser", "Number of rows: $rowCount, number of vals $valuesCount", )
//        for (i in 0 until rowCount){
//            val pos0 = i * valuesCount
//            val pos1 = i * valuesCount + valuesCount
//
//            val row = try {
//                valuesOnly.subList(pos0, pos1)
//            } catch (e: IndexOutOfBoundsException){
//                Log.e("DocumentParser", "creating table\n ${e.printStackTrace()}")
//                valuesOnly.subList(pos0, pos1-1)
//            }
//
//            if(DEBUG_TABLE_SPLITTING){
//                Log.d("Row$i", row.toString() + "pos0: $pos0, pos1: $pos1")
//            }
//            table.add(row)
//        }
//        return table
//    }


//    fun parseData(context: Context, textDocument: String, valueNames: List<String>, valuesCount: Int): MutableList<Map<String, String>> {
//        /**
//         * Parses a structured text document and extracts information from the table.
//         *
//         * @param context
//         * @param valuesCount Number of values per row to put the table in the right shape
//         * @param textDocument Text document in a specific format (form https://services.swpc.noaa.gov/).
//         *                     The document contains descriptions and table data separated by specific markers.
//         *                     Format: Opening body tag, document content, and closing body tag.
//         * @return table, where each row is a list of string values extracted from the table.
//         * @throws ParseException if the input text document does not conform to the expected format.
//         */
//        val dataTable = extractDataTable(valuesCount, textDocument)
//        dataTableMapped = mapParsedValuesToValueNames(
//            context, dataTable, valueNames, ExceptionHandler.getInstance(context))
//        return dataTableMapped
//    }

