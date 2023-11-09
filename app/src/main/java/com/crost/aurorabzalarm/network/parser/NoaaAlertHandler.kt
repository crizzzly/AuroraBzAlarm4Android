package com.crost.aurorabzalarm.network.parser

import com.crost.aurorabzalarm.repository.NoaaAlerts.GEO_STORM_ALERT_IDs
import com.crost.aurorabzalarm.repository.NoaaAlerts.KP_ALERT_IDs
import com.crost.aurorabzalarm.repository.NoaaAlerts.KP_WARNING_IDs
import com.crost.aurorabzalarm.utils.datetime_utils.convertUtcToLocal
import com.crost.aurorabzalarm.utils.datetime_utils.parseDateTimeString
import org.json.JSONArray
import java.time.LocalDateTime

data class NoaaAlert(
    var id: String,
    var issueDatetime: LocalDateTime,
    var message: String,
)


class NoaaAlertHandler() {
    val kpWarningIds = KP_WARNING_IDs
    val kpAlertIds = KP_ALERT_IDs
    val geoStormIds = GEO_STORM_ALERT_IDs

    val kpWarningsList = mutableListOf<NoaaAlert>()
    val kpAlertsList = mutableListOf<NoaaAlert>()
    val geoAlertsList = mutableListOf<NoaaAlert>()
    val lists = listOf(
        geoAlertsList, kpAlertsList, kpWarningsList
    )



    fun handleAlerts(alerts: List<NoaaAlert>){
        checkForRelevantAlerts(alerts)
    }



    private fun checkForRelevantAlerts(alerts: List<NoaaAlert>){
        for (alert in alerts){
            if (alert.id in KP_WARNING_IDs){
                kpWarningsList.add(alert)
            }
            else if (alert.id in KP_ALERT_IDs){
                kpAlertsList.add(alert)
            }
            else if (alert.id in GEO_STORM_ALERT_IDs){
                geoAlertsList.add(alert)
            }
        }
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
}

