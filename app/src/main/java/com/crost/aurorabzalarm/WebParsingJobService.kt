package com.crost.aurorabzalarm

//import android.app.ForegroundServiceStartNotAllowedException
//import android.app.Notification
//import android.content.Context
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationBuilderWithBuilderAccessor
//import androidx.work.CoroutineWorker
//import androidx.work.ForegroundInfo
//import androidx.work.WorkerParameters
//
//
//private const val TAG = "ParsingWorker"
//class WebParsingWorker(
//    ctx: Context,
//    params: WorkerParameters
//): CoroutineWorker(ctx,params) {
//
//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        return ForegroundInfo(
//            1, createNotification()
//        )
//    }
//    @RequiresApi(Build.VERSION_CODES.S)
//    override suspend fun doWork(): Result {
//        return try {
//            setForeground(getForegroundInfo())
//
//            Result.success()
//        } catch (throwable: Throwable) {
//            Result.failure()
//        } catch (e: IllegalStateException){
//            Result.failure()
//        } catch (e: ForegroundServiceStartNotAllowedException){
//            Result.failure()
//        }
//
//    }
//
//
//
//
//
//}
////@SuppressLint("SpecifyJobSchedulerIdRange")
////class WebParsingJobService : JobService() {
////    override fun onCreate() {
////        super.onCreate()
////        Log.d("WebParsingJobService", "JobService created")
////    }
////
////    override fun onStartJob(params: JobParameters?): Boolean {
////        Log.d("WebParsingJobService", "running")
////
////        val extras = params?.extras
////        val viewModelId = extras?.getString("viewModelId")
////        val viewModel: DataViewModel = viewModel(
////            factory = DataViewModel.provideFactory(
////                owner = LocalSavedStateRegistryOwner.current,
////                defaultArgs = null
////
////            )
////        )
////        // Use viewModel and viewModelId in your job processing logic
////        GlobalScope.launch(Dispatchers.IO) {
////            try {
////                val sdp = viewModel?.let {
////                    SatelliteDataParser(it)
////                }
////                val res = sdp?.parseSatelliteData()
////                // Handle the result here
////            } catch (e: Exception) {
////                Log.e("WebParsingJobService", "Job failed: ${e.message}")
////            } finally {
////                jobFinished(params, false) // Finish the job
////            }
////        }
////        return true // Job is running asynchronously
////
////    }
////
////    override fun onStopJob(params: JobParameters?): Boolean {
////        // Called if the job is canceled before it is finished
////        // Return true to reschedule the job if necessary
////        return false
////    }
////}
