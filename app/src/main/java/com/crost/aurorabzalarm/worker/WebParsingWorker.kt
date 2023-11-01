package com.crost.aurorabzalarm.worker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.crost.aurorabzalarm.R
import com.crost.aurorabzalarm.utils.Constants
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WebParsingWorker(
    var ctx: Context,
    var viewModel: DataViewModel,
    params: WorkerParameters,
): CoroutineWorker(ctx,params) {

    private val notificationManager = ctx.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

    
    override suspend fun doWork(): Result {
        Log.d("WebParsingWorker", "running")
         return try {
                    viewModel.fetchSpaceWeatherData()

                Result.success()
            } catch (throwable: Throwable) {
                Log.e("WebParsingWorker", throwable.stackTraceToString())

                val text = "WebParsingWorker\n${throwable.message}"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()
                Result.failure()
            } catch (e: IllegalStateException){
                Log.e("WebParsingWorker", e.stackTraceToString())

                val text = "WebParsingWorker\n${e.message}"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                Result.failure()
            }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val notificationId = 19

        val notificationChannel = NotificationChannel(
            Constants.CHANNEL_ID,
            "AuroraBzAlarm",
            NotificationManager.IMPORTANCE_HIGH
        )

        try {
            notificationManager.createNotificationChannel(notificationChannel)
        } catch (e: Exception){
            Log.e("App: createNotificChannel", e.stackTraceToString())

            val text = "createNotificationManager-app\n${e.message}"
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
            toast.show()
        }

        Log.d("AuroraNotificationService", "Showing Notification")
        val notification = NotificationCompat.Builder(ctx, id)
            .setContentTitle(title)
            .setContentText("$time\nBz is currently at ${viewModel.latestAceState.value!!.bz}\n" +
                    "Hemispheric Power at ${viewModel.latestHpState.value!!.hpNorth}")
            .setSmallIcon(androidx.core.R.drawable.notification_bg) //--> find icon!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setVibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            .build()

        return ForegroundInfo(notificationId, notification)
    }
}


class MyWorkerFactory(
    private val viewModel: DataViewModel
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WebParsingWorker::class.java.name -> {
                WebParsingWorker(appContext, viewModel, workerParameters)
            }
            else -> null
        }
    }
}