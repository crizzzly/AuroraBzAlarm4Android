package com.crost.aurorabzalarm

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crost.aurorabzalarm.ui.DataViewModel
import com.crost.aurorabzalarm.ui.ViewModelFactory
import com.crost.aurorabzalarm.worker.MyWorkerFactory
import com.crost.aurorabzalarm.worker.WebParsingWorker
import java.util.concurrent.TimeUnit
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels

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
        val application = applicationContext
        ViewModelFactory.init(this)
        viewModel = ViewModelFactory.getDataViewModel()


        permissionManager = PermissionManager()

//        WorkManagerInitializer().
        WorkManager.initialize(this, workManagerConfiguration)
        // TODO: Get It To Work!
        val workManager = WorkManager.getInstance(application)
        val parsingWorkRequest = PeriodicWorkRequestBuilder<WebParsingWorker>(60, TimeUnit.SECONDS)
            .addTag("WebParsingWorker")
            .setConstraints(Constraints(NetworkType.CONNECTED))
            .build()
        workManager.enqueue(parsingWorkRequest)


    }
}