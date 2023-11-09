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
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import com.crost.aurorabzalarm.worker.MyWorkerFactory
import com.crost.aurorabzalarm.worker.WebParsingWorker
import java.util.concurrent.TimeUnit

// androidx.fragment:fragment-ktx
class AuroraScopeEuropeApp: Application(), Configuration.Provider {
//    private lateinit var permissionManager: PermissionManager
    private lateinit var dataViewModel:  DataViewModel// by viewModels()
    private lateinit var fileLogger: FileLogger

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(
                MyWorkerFactory(dataViewModel)) // Pass your DataViewModel instance here
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        fileLogger=  FileLogger.getInstance(this.applicationContext)
        dataViewModel = DataViewModel(this)
//        permissionManager = PermissionManager()

        Log.d("App-onCreate", "initializing WorkManager")
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


        // Create Notification Channel

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
            val msg = "App: createNotificChannel\n${e.printStackTrace()}"
            fileLogger.writeLogsToInternalStorage(this.applicationContext, msg)
            val text = "createNotificationManager-app\n${e.message}"
            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
            toast.show()
        }
  }
}

