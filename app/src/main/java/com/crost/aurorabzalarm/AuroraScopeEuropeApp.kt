package com.crost.aurorabzalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crost.aurorabzalarm.ui.screens.settings.SettingsViewModel
import com.crost.aurorabzalarm.utils.Constants.CHANNEL_ID
import com.crost.aurorabzalarm.utils.Constants.WORKER_REPEAT_INTERVAL
import com.crost.aurorabzalarm.utils.ExceptionHandler
import com.crost.aurorabzalarm.utils.FileLogger
import com.crost.aurorabzalarm.viewmodels.DataViewModel
import com.crost.aurorabzalarm.worker.MyWorkerFactory
import com.crost.aurorabzalarm.worker.WebParsingWorker
import java.util.concurrent.TimeUnit

const val DEBUG_APP = false
// androidx.fragment:fragment-ktx
class AuroraScopeEuropeApp: Application(), Configuration.Provider {
//    private lateinit var permissionManager: PermissionManager
    private lateinit var dataViewModel:  DataViewModel// by viewModels()
    private lateinit var settingsViewModel: SettingsViewModel// by viewModels()
    private lateinit var exceptionHandler: ExceptionHandler
    private lateinit var fileLogger: FileLogger

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(
                MyWorkerFactory(dataViewModel)) // Pass your DataViewModel instance here
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        exceptionHandler = ExceptionHandler.getInstance(this)
        fileLogger=  FileLogger.getInstance(this.applicationContext)
        dataViewModel = DataViewModel(this)
        settingsViewModel = SettingsViewModel(this)
//        permissionManager = PermissionManager()

        if (DEBUG_APP) Log.d("App-onCreate", "initializing WorkManager")
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
        if (DEBUG_APP) Log.d("App", "WorkManager enqueued")


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
            exceptionHandler.handleExceptions(
                this,"App: createNotificChannel", e.stackTraceToString()
            )
        }
  }
}

