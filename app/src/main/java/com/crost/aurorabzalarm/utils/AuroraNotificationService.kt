package com.crost.aurorabzalarm.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.crost.aurorabzalarm.MainActivity
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import com.crost.aurorabzalarm.viewmodels.DataViewModel
//import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuroraNotificationService(
    private val context: Context
) {
    private val fileLogger = FileLogger.getInstance(context)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationId = 17

    // TODO: Check if urgent message is sth:
    //  https://developer.android.com/develop/ui/views/notifications/build-notification#urgent-message

    fun showBasicNotification(context: Context, viewModel: DataViewModel ){
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        Log.d("AuroraNotificationService", "Showing Notification")


        // Notification Builder

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Aurora Probability: Bz has fallen!")
            .setContentText("$time: Bz is currently at ${viewModel.latestAceState.value!!.bz}\n" +
                    "Hemispheric Power at ${viewModel.latestHpState.value!!.hpNorth}")
            .setSmallIcon(androidx.core.R.drawable.notification_bg) //--> find icon!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setVibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)) -> how does this work?
            .build()

        try {
            Log.d("AuroraNotificationService", "notify ...")
            notificationManager.notify(
                notificationId,
                notification
            )
            Log.d("AuroraNotificationService", "notify ... done!")
        } catch (e: Exception){
            val msg = "AuroraNotificationService ${e.stackTraceToString()}"
            fileLogger.writeLogsToInternalStorage(context, msg)
            Log.e("AuroraNotificationService", e.stackTraceToString())

            val text = "AuroraNotificationService\n${e.message}"
            val toast = Toast.makeText(context, text, Toast.LENGTH_LONG)
            toast.show()
        }
    }
}