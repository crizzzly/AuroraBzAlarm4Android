package com.crost.aurorabzalarm

import android.app.Application
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AuroraScopeEurope: Application(), Configuration.Provider {
    private lateinit var permissionManager: PermissionManager

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(MyWorkerFactory()) // Pass your DataViewModel instance here
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        val application = applicationContext
        ViewModelFactory.init(this)

        permissionManager = PermissionManager()

//        WorkManagerInitializer().
        WorkManager.initialize(this, getWorkManagerConfiguration())
        // TODO: Get It To Work!
        val workManager = WorkManager.getInstance(application)
        val parsingWorkRequest = PeriodicWorkRequestBuilder<WebParsingWorker>(60, TimeUnit.SECONDS)
            .build()
        workManager.enqueue(parsingWorkRequest)
    }
}