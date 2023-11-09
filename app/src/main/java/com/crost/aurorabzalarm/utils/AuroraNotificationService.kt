package com.crost.aurorabzalarm.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import com.crost.aurorabzalarm.viewmodels.DataViewModel
//import com.crost.aurorabzalarm.viewmodels.AuroraViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuroraNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
//    private val viewModel = AuroraViewModelFactory.getDataViewModel()
    private val notificationId = 17

    // TODO: Check if urgent message is sth:
    //  https://developer.android.com/develop/ui/views/notifications/build-notification#urgent-message

    fun showBasicNotification(viewModel: DataViewModel ){

//        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
//            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//            putExtra(Settings.EXTRA_CHANNEL_ID, myNotificationChannel.getId())
//        }
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        
        Log.d("AuroraNotificationService", "Showing Notification")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Aurora Probability: Bz has fallen!")
            .setContentText("$time: Bz is currently at ${viewModel.latestAceState.value!!.bz}\n" +
                    "Hemispheric Power at ${viewModel.latestHpState.value!!.hpNorth}")
            .setSmallIcon(androidx.core.R.drawable.notification_bg) //--> find icon!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
//            .setContentIntent()
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
            FileLogger.getInstance(context).writeLogsToInternalStorage(context, msg)
            Log.e("AuroraNotificationService", e.stackTraceToString())

            val text = "AuroraNotificationService\n${e.message}"
            val toast = Toast.makeText(context, text, Toast.LENGTH_LONG)
            toast.show()
        }
    }
}