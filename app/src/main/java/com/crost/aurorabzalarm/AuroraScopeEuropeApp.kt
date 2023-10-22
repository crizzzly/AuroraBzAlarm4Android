package com.crost.aurorabzalarm

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crost.aurorabzalarm.Constants.WORKER_REPEAT_INTERVALL
import com.crost.aurorabzalarm.ui.DataViewModel
import com.crost.aurorabzalarm.ui.ViewModelFactory
import com.crost.aurorabzalarm.worker.MyWorkerFactory
import com.crost.aurorabzalarm.worker.WebParsingWorker
import java.util.concurrent.TimeUnit

class AuroraScopeEuropeApp: Application(), Configuration.Provider {
    private lateinit var permissionManager: PermissionManager
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
        permissionManager = PermissionManager()

//      Remove entry in manifest when deleting WorkManager.init
        WorkManager.initialize(this, workManagerConfiguration)
        val workManager = WorkManager.getInstance(this)
        val parsingWorkRequest =
            PeriodicWorkRequestBuilder<WebParsingWorker>(
                WORKER_REPEAT_INTERVALL, TimeUnit.SECONDS
            )
            .addTag("WebParsingWorker")
            .setConstraints(Constraints(NetworkType.CONNECTED))
            .build()
        workManager.enqueue(parsingWorkRequest)
  }
}