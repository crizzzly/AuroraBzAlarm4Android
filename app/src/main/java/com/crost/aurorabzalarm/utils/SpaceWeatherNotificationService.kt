package com.crost.aurorabzalarm.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.crost.aurorabzalarm.MainActivity
import com.crost.aurorabzalarm.R
import com.crost.aurorabzalarm.data.NoaaAlert
import com.crost.aurorabzalarm.data.NoaaAlerts.GEO_STORM_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_ALERT_IDs
import com.crost.aurorabzalarm.data.NoaaAlerts.KP_WARNING_IDs
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val DEBUG = false

class SpaceWeatherNotificationService(
    private val context: Context
) {
    private val exceptionHandler = ExceptionHandler.getInstance(context)
    private val fileLogger = FileLogger.getInstance(context)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val notificationIdBasic = 17
    private val notificationIdNoaaAlert = 18
    private val idKpAlert = 19
    private val idKpWarning = 20
    private val idSolarStorm = 21


    private fun getIdAndTitle(alert: NoaaAlert):List<Any>{
        val idTitle = mutableListOf<Any>()

        when(alert.id){
            in KP_WARNING_IDs -> {
                idTitle.add(idKpWarning)
                val kLevel = alert.id[2]
                idTitle.add("KP$kLevel Predicted!")
            }
            in KP_ALERT_IDs -> {
                idTitle.add(idKpAlert)
                val kLevel = alert.id[2]
                idTitle.add("KP$kLevel Detected!")
            }
            in GEO_STORM_ALERT_IDs -> {
                idTitle.add(idSolarStorm)
                val stormLevel = alert.id[1]
                idTitle.add("G$stormLevel Detected!")
            }
        }
        return idTitle
    }

    fun showNoaaAlert(alert: NoaaAlert){
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        if (DEBUG) Log.d("SpaceWeatherNotificationService", "Showing Noaa Alert")

        val idAndTitle = getIdAndTitle(alert)

        val title = "${idAndTitle[1]}: ${alert.datetime}"
        val id = idAndTitle[0] as Int
        val text = alert.message

        val notification = createNotification(title, text)
        notify(id, notification)
    }


    fun showSpaceWeatherNotification(bz: Double, hp: Int){
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        if (DEBUG) Log.d("SpaceWeatherNotificationService", "Showing SpaceWeather Notification")

        val title = "Aurora Probability: Bz has fallen!"
        val text = "$time: Bz is currently at $bz"

        val notification = createNotification(title, text)

        notify(notificationIdBasic, notification)
    }

    private fun notify(id: Int, notification: Notification) {
        try {
            if (DEBUG) Log.d("SpaceWeatherNotificationService", "notify ...")
            notificationManager.notify(
                id,
                notification
            )
            if (DEBUG) Log.d("SpaceWeatherNotificationService", "notify ... done!")
        } catch (e: Exception){
            exceptionHandler.handleExceptions(
                context, "SpaceWeatherNotificationService", e.stackTraceToString()
            )
        }
    }

    private fun createNotification(title: String, text: String): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(text))
            .setSmallIcon(R.drawable.aurora_sw) //--> find icon!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setVibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)) -> how does this work?
            .build()
    }
}