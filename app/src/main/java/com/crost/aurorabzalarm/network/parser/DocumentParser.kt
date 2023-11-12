package com.crost.aurorabzalarm.network.parser

import android.util.Log
import com.crost.aurorabzalarm.data.ImfData
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.SolarWindData
import com.crost.aurorabzalarm.utils.datetime_utils.dateTimeStringToLocalDateTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


const val DEBUG_DOCUMENT_SPLITTING = false
const val DEBUG_TABLE_SPLITTING = false
const val DEBUG_LIST = false


class DocumentParser {

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
                bt = entry[6].toDouble()
            )
        }
        return dataEntries
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

    private fun convertListStringToList(jsonData: String): List<List<String>> {
        val tmp = jsonData.trimIndent()
        if (DEBUG_LIST) Log.d("parseIMF/ACEJson", "tmp: $tmp")
        val listType = object : TypeToken<List<List<String>>>() {}.type

        return Gson().fromJson(tmp, listType)
    }
}
