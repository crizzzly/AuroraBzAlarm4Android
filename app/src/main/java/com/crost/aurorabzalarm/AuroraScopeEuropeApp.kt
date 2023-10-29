package com.crost.aurorabzalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import android.widget.Toast
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import com.crost.aurorabzalarm.utils.Constants.WORKER_REPEAT_INTERVAL
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import com.crost.aurorabzalarm.viewmodels.ViewModelFactory
import com.crost.aurorabzalarm.worker.MyWorkerFactory
import com.crost.aurorabzalarm.worker.WebParsingWorker
import java.util.concurrent.TimeUnit

class AuroraScopeEuropeApp: Application(), Configuration.Provider {
//    private lateinit var permissionManager: PermissionManager
    private lateinit var viewModel: DataViewModel

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(
                MyWorkerFactory(viewModel)) // Pass your DataViewModel instance here
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        ViewModelFactory.init(this)

//        val application = applicationContext
        viewModel = ViewModelFactory.getDataViewModel()
//        permissionManager = PermissionManager()

        Log.d("App", "initializing WorkManager")
//      Remove entry in manifest when deleting WorkManager.init
        WorkManager.initialize(this, workManagerConfiguration)
        val workManager = WorkManager.getInstance(this)
        val parsingWorkRequest =
            PeriodicWorkRequestBuilder<WebParsingWorker>(
                WORKER_REPEAT_INTERVAL, TimeUnit.SECONDS
            )
            .addTag("WebParsingWorker")
            .setConstraints(Constraints(NetworkType.CONNECTED))
            .build()
        workManager.enqueue(parsingWorkRequest)
        Log.d("App", "WorkManager enqueued")

        // Notification
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "AuroraBzAlarm",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.createNotificationChannel(notificationChannel)
        } catch (e: Exception){
            Log.e("App: createNotificChannel", e.stackTraceToString())

            val text = "createNotificationManager-app\n${e.message}"
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
            toast.show()
        }
  }
}

