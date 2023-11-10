package com.crost.aurorabzalarm.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat.Builder
import com.crost.aurorabzalarm.MainActivity
import com.crost.aurorabzalarm.R


class DataFetchingForegroundService: Service() {

    private val channelId = "data_fetch_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your data fetching logic goes here
        Toast.makeText(this, "Data Fetching in progress...", Toast.LENGTH_SHORT).show()

        // Stop the service after data fetching is done
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Data Fetch Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        return Builder(this, channelId)
            .setContentTitle("Data Fetching Service")
            .setContentText("Data fetching in progress...")
            .setSmallIcon(R.drawable.aurora)
            .setContentIntent(pendingIntent)
            .build()
    }

}