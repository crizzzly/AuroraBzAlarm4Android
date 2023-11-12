package com.crost.aurorabzalarm.data

import android.util.Log
import com.crost.aurorabzalarm.data.NoaaAlerts.GEO_STORM_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_WARNING_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.MAX_MINUTES_BETWEEN_ALERT_AND_NOW
import com.crost.aurorabzalarm.utils.datetime_utils.calculateTimeDifferenceFromNow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration
import java.time.LocalDateTime

const val DEBUG = true
data class NoaaAlert(
    var id: String,
    var datetime: LocalDateTime,
    var message: String,
)


class NoaaAlertDataHandler() {
    val kpWarningIds = KP_WARNING_IDs
    val kpAlertIds = KP_ALERT_IDs
    val geoStormIds = GEO_STORM_ALERT_IDs

    val kpWarning = MutableStateFlow(
        NoaaAlert("0", LocalDateTime.now(), "")
    )
    val kpAlert = MutableStateFlow(
        NoaaAlert("0", LocalDateTime.now(), "")
    )
    val geoAlert = MutableStateFlow(
        NoaaAlert("0", LocalDateTime.now(), "")
    )
    val lists = listOf(
        geoAlert, kpAlert, kpWarning
    )



    fun checkForRelevantAlerts(alerts: List<NoaaAlert>):List<NoaaAlert> {
        for (alert in alerts.asReversed()){
            val duration = calculateTimeDifferenceFromNow(alert.datetime)
            if(duration.toMinutes() < MAX_MINUTES_BETWEEN_ALERT_AND_NOW){
                if (DEBUG) Log.d("NoaaAlert:", alert.id+"\n"+alert.message)
                when (alert.id) {
                    in KP_WARNING_IDs -> {
                        kpWarning.value = alert
                    }
                    in KP_ALERT_IDs -> {
                        kpAlert.value = alert
                    }
                    in GEO_STORM_ALERT_IDs -> {
                        geoAlert.value = alert
                    }
                }
            }
        }
        return listOf(kpAlert.value, kpWarning.value, geoAlert.value)
    }

    private fun calculateTimeDifference(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Duration? {
        val now = LocalDateTime.now()

        return Duration.between(startDateTime, endDateTime)
    }

//    fun parseJson(jsonData: String): List<NoaaAlert> {
//        val alerts = mutableListOf<NoaaAlert>()
//        val jsonArray = JSONArray(jsonData)
//
//        for (i in 0 until jsonArray.length()) {
//            val jsonObject = jsonArray.getJSONObject(i)
//            val productId = jsonObject.getString("product_id")
//            val dtString = jsonObject.getString("issue_datetime")
//            val issueDatetime = dateTimeStringToLocalDateTime(dtString)
//            val message = jsonObject.getString("message")
//            val alert = NoaaAlert(productId, issueDatetime, message)
//            alerts.add(alert)
//        }
//        return alerts
//    }
//
//    private fun dateTimeStringToLocalDateTime(dtString: String): LocalDateTime {
//        val utc = parseDateTimeString(dtString)
//        return convertUtcToLocal(utc)
//    }
}

