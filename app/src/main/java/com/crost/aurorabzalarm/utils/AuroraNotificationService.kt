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
import com.crost.aurorabzalarm.network.parser.NoaaAlert
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val DEBUG = true

class AuroraNotificationService(
    private val context: Context
) {
    private val exceptionHandler = ExceptionHandler.getInstance(context)
    private val fileLogger = FileLogger.getInstance(context)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val notificationIdBasic = 17
    private val notificationIdNoaaAlert = 18

    // TODO: Check if urgent message is sth:
    //  https://developer.android.com/develop/ui/views/notifications/build-notification#urgent-message

    fun showNoaaAlert(alert: NoaaAlert){
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        if (DEBUG) Log.d("AuroraNotificationService", "Showing Noaa Alert")

        val title = "Noaa ${alert.id}, time: ${alert.issueDatetime}"
        val text = alert.message

        val notification = createNotification(title, text)
        notify(notificationIdNoaaAlert, notification)
    }


    fun showSpaceWeatherNotification(bz: Double, hp: Int){
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        if (DEBUG) Log.d("AuroraNotificationService", "Showing SpaceWeather Notification")

        val title = "Aurora Probability: Bz has fallen!"
        val text = "$time: Bz is currently at ${bz}\n" +
                "Hemispheric Power at ${hp}"

        val notification = createNotification(title, text)

        notify(notificationIdBasic, notification)
    }

    private fun notify(id: Int, notification: Notification) {
        try {
            Log.d("AuroraNotificationService", "notify ...")
            notificationManager.notify(
                id,
                notification
            )
            Log.d("AuroraNotificationService", "notify ... done!")
        } catch (e: Exception){
            exceptionHandler.handleExceptions(
                context, "AuroraNotificationService", e.stackTraceToString()
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